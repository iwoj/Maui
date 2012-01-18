// ======================================================================
// com.bitmovers.maui.engine.resourcemanager.ResourceManager
// ======================================================================

package com.bitmovers.maui.engine.resourcemanager;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.awt.*;
import com.bitmovers.maui.profiler.Profiler;
import com.bitmovers.maui.*;
import com.bitmovers.maui.components.*;
import com.bitmovers.maui.engine.*;
import com.bitmovers.maui.engine.cachemanager.*;
import com.bitmovers.maui.engine.logmanager.*;


// ======================================================================
// <<SINGLETON>> CLASS: ResourceManager
// ======================================================================

/** The ResourceManager provides resources to components of Maui 0.8.
  * Such resources include images, text/html/wml files, etc.
  *
  * @author Patrick Gibson (patrick@bitmovers.com)
  *
  */

public class ResourceManager
{
	// --------------------------------------------------------------------

	// Use a static initialiser to start up this class
	private static ResourceManager thisInstance = new ResourceManager();

	private static final String CACHE_DOMAIN = "ResourceManager";
	private File systemResourceFile = null;
	private Hashtable additionalResources = new Hashtable();
	private Hashtable resourceHash = new Hashtable();
	private Hashtable componentReferences = new Hashtable ();
	private Hashtable crossReferences = new Hashtable ();
	private Hashtable cacheCrossReferences = new Hashtable ();
	private Hashtable archiveHash = new Hashtable (5);
	private boolean caching = false;


	class AdditionalResource
	{
		private File file;
		private Vector contents;
	
		protected AdditionalResource (File aFile, int aSize)
		{
			file = aFile;
			contents = new Vector (aSize);
		}
		
		protected File getFile ()
		{
			return file;
		}
		
		protected void addElement (Object aElement)
		{
			contents.addElement (aElement);
		}
		
		protected Vector getContents ()
		{
			return contents;
		}
	}
	
	// --------------------------------------------------------------------
	// CONSTRUCTOR: ResourceManager
	// --------------------------------------------------------------------

	/** This constructor is private and shall never be called by anyone
	  * but the static initialiser.
	  *
	  */

	private ResourceManager()
	{
	}


	// --------------------------------------------------------------------
	// METHOD: initialise
	// --------------------------------------------------------------------
	
	public void initialise()
	{
		try
		{
			ServerConfigurationManager configManager = ServerConfigurationManager.getInstance();

			String mauiResourceFile = configManager.getProperty(ServerConfigurationManager.MAUI_RESOURCES);
			String mauiApplicationFolder = configManager.getProperty(ServerConfigurationManager.MAUI_APPLICATION_LOCATION);

			this.setSystemResources(new File(mauiResourceFile));
			this.addResource(new File(mauiApplicationFolder));
		}
		catch (Exception exception)
		{
			System.out.println(new ErrorString(exception, "ResourceManager.initialise()"));
		}

		System.out.println(new DebugString("[ResourceManager] - Started."));
	}


	// --------------------------------------------------------------------
	// METHOD: getInstance
	// --------------------------------------------------------------------

	/** getInstance() gets an instance of the ResourceManager.
	  *
	  */
  
	public static ResourceManager getInstance()
	{
		return ResourceManager.thisInstance;
	}

	// --------------------------------------------------------------------
	// METHOD: removeCrossReference
	// --------------------------------------------------------------------

	/** Remove a cross reference between a resource name and an MComponent which
	  * can create the resource (eg. MButton).
	  *
	  * @param aComponent The MComponent to be cross referenced
	  *
	  */
	public void removeCrossReference (MComponent aComponent)
	{
		String theCrossReference = (String) componentReferences.get (aComponent);
		if (theCrossReference != null)
		{
			componentReferences.remove (aComponent);
			Vector theComponents = (Vector) crossReferences.get (theCrossReference);
			theComponents.remove (aComponent);
			if (theComponents.size () == 0)
			{
				crossReferences.remove (theCrossReference);
				purgeCache (theCrossReference);
			}
		}
	}

	// --------------------------------------------------------------------
	// METHOD: getCrossReference
	// --------------------------------------------------------------------

	/** Get a cross reference between a resource name and an MComponent which
	  * can create the resource (eg. MButton).
	  *
	  * @param aReference If this is a string then an MComponent will be returned.
	  *					  If this is an MComponent then a String will be returned.
	  *
	  * @return The cross referenced MComponent or String, or null if not found
	  */
	public Object getCrossReference (Object aReference)
	{
		Object retVal = null;
		
		if (aReference instanceof String)
		{
			retVal = (Vector) crossReferences.get (aReference);
			if (retVal != null)
			{
				retVal = ((Vector) retVal).firstElement ();
			}
		}
		else
		{
			retVal = componentReferences.get (aReference);
		}
		return retVal;
	}
					
	// --------------------------------------------------------------------
	// METHOD: addCrossReference
	// --------------------------------------------------------------------

	/** Add a cross reference between a resource name and an MComponent which
	  * can create the resource (eg. MButton).
	  *
	  * @param aCrossReference The cross reference name
	  * @param aComponent The MComponent to be cross referenced
	  *
	  */
	public void addCrossReference (String aCrossReference, MComponent aComponent)
	{
		String theCrossReference = aCrossReference.replace (' ', '_');
		removeCrossReference (aComponent);
		Vector theComponents = (Vector) crossReferences.get (theCrossReference);
		if (theComponents == null)
		{
			theComponents = new Vector (30, 30);
			crossReferences.put (theCrossReference, theComponents);
		}
		componentReferences.put (aComponent, theCrossReference);
		theComponents.addElement (aComponent);
	}

	// --------------------------------------------------------------------
	// METHOD: setSystemResources
	// --------------------------------------------------------------------

	/** setSystemResources() sets the file which will be used as to get the
	  * core resources for Maui. This file will always be searched through
	  * first before subsequent files. This method can only be called once.
	  * Subsequent calls will throw a SystemResourcesAlreadyDefinedException.
	  *
	  */
  
	public void setSystemResources(File file) throws SystemResourcesAlreadyDefinedException, ResourceNotFoundException
	{
		if (this.systemResourceFile == null)
		{
			this.systemResourceFile = file;
		}
		else
		{
			throw new SystemResourcesAlreadyDefinedException();
		}

		try
		{
			this.addResource(file);
		}
		catch (ResourceNotFoundException exception)
		{
			throw new ResourceNotFoundException("The system resources file '" + file.getName() + "' could not be found. Please check the maui.resources property and try again.");
		}
	}
  

	// --------------------------------------------------------------------
	// METHOD: setCaching
	// --------------------------------------------------------------------

	/** setCaching() sets the status of caching in the ResourceManager. If
	  * it is on, The ResourceManager will make use of the CacheManager
	  * whenever a resource is requested.
	  *
	  */
  
	public void setCaching(boolean cacheMeIfYouCan)
	{
		this.caching = cacheMeIfYouCan;
	}
  

	// --------------------------------------------------------------------
	// METHOD: addResource
	// --------------------------------------------------------------------

	/** addResource() takes a file or folder and obtains all the resources
	  * held within.
	  *
	  */

	public void addResource(String file) throws ResourceNotFoundException
	{
		this.addResource(new File(file));
	}
  
  
	// --------------------------------------------------------------------
	// METHOD: addResource
	// --------------------------------------------------------------------

	/** addResource() takes a file or folder and obtains all the resources
	  * held within.
	  *
	  */
  
	public void addResource(File file) throws ResourceNotFoundException
	{
		// [1] Test for a null file
		if (file == null)
		{
			throw new ResourceNotFoundException("ResourceManager.addResource(): Received a null File object.");
		}

		// [2] Test for the file's existence
		if (!file.exists())
		{
			throw new ResourceNotFoundException("ResourceManager.addResource(): The file '" + file.getName() + "' could not be found.");
		}
    
		// [3] If the file is a folder, recursively search through and process
		//     all files contained within.
		if (file.isDirectory())
		{
			if (file.getPath ().indexOf ("resource.frk") == -1)
			{
				String[] files = file.list(new ResourceArchiveFilter());

				for (int i = 0; i < files.length; i++)
				{
					this.addResource(new File(file.getPath() + File.separator + files[i]));
				}
			}
		}
		// Scan and load the file contents
		else
		{
			try
			{
				this.addResourceFile(file);
				System.out.println(new DebugString("[ResourceManager] Resources loaded from " + file.getName()));
			}
			catch (MauiException exception)
			{
				System.out.println(new ErrorString(exception));
			}
		}
  }


	// --------------------------------------------------------------------
	// METHOD: addResourceFile
	// --------------------------------------------------------------------

	/** addResourceFile() takes a resource file, verifies its format, and
	  * proceeds to build a list of files contained within. Resource files
	  * must be in the ZIP (JAR) file format.
	  *
	  */
  
  public void addResourceFile(File file) throws InvalidResourceArchiveException, DuplicateResourceArchiveException
  {
    ZipFile resource = null;

    // [1] Test for the validity of the file
    if ((resource = this.getZipFileFromFile(file)) == null)
    {
      throw new InvalidResourceArchiveException("The file '" + file.getName() + "' was not a valid ZIP/JAR file. Please verify and try again.");
    }
    
    // [2] Add the file into the list of resources
	AdditionalResource theResource = new AdditionalResource (file, resource.size ());
    {
      if (!this.additionalResources.containsKey(file.getName()))
      {
        this.additionalResources.put(file.getPath (), theResource);
      }
      else
      {
        try
        {
        	resource.close ();
        }
        catch (IOException e)
        {
        }
        throw new DuplicateResourceArchiveException("Skipping archive '" + file.getName() + "' because it has already been added.");
      }
    }
    
    // [3] If we've made it this far, we can go ahead and scan the ZIP
    //     file and add all of the resources to the resources Hashtable.
    {
      Enumeration entries = resource.entries();
      ResourceFileFilter filter = new ResourceFileFilter();
      while (entries.hasMoreElements())
      {
        ZipEntry entry = (ZipEntry)entries.nextElement();
        
        // If the filename is accepted by the filter, add to the resource
        // hashtable (if it isn't already in there)
        String entryName = entry.getName();
        
        if (filter.accept(null, entryName))
        {
          if (!this.resourceHash.containsKey(entryName))
          {
          	theResource.addElement (entryName);
            this.resourceHash.put(entryName, file.getPath ());
            
          }
          else
          {
            System.out.println(new WarningString("ResourceManager.addResource(): skipping file '" + entryName + "' because it already exists."));
          }
        }
      }
      
      try
      {
      	resource.close ();
      }
      catch (IOException e)
      {
      }
    }
  }
  
  /**
  * Remove a resource file from the archive
  *
  * @param aFileName The name of the file to remove
  */
  public void removeResourceFile (String aFileName)
  {
  	AdditionalResource theResource = (AdditionalResource) additionalResources.get (aFileName);
  	if (theResource != null)
  	{
  		additionalResources.remove (aFileName);
  		Enumeration theResources = theResource.getContents ().elements ();
  		CacheDomain theDomain = getCacheDomain ();
  		while (theResources.hasMoreElements ())
  		{
  			String theResourceName = (String) theResources.nextElement ();
  			resourceHash.remove (theResourceName);
  			theDomain.remove (theResourceName);
  		}
  	}
  }

  // --------------------------------------------------------------------
  // METHOD: getZipFileFromFile
  // --------------------------------------------------------------------
  
  /** getZipFileFromFile() tests a given File to see if it is a valid
    * ZIP file. If it is found to be valid, a ZipFile object is returned.
    * If it is not a ZIP file, null will be returned.
    *
    */
  
  private ZipFile getZipFileFromFile(File file)
  {
    ZipFile zipFile = null;

    try
    {
      zipFile = new ZipFile(file);
    }
    catch (Exception exception)
    {
      return null;
    }
    
    return zipFile;
  }


  // --------------------------------------------------------------------
  // METHOD: getResource
  // --------------------------------------------------------------------
  
  /** getResource() is a temporary method to provide backwards-
    * compatibility with the old JarResources class. This method is not
    * permanent.
    *
    * @deprecated Use getResourceBytes() or getResourceAsString() instead.
    *
    */
  
  public byte[] getResource(String path)
  	throws ResourceNotFoundException
  {
		//try
		//{
		  return this.getResourceBytes(path);
		//}
		//catch (ResourceNotFoundException exception)
		//{
		  //System.err.println(new ErrorString(exception, "ResourceManager.getResource()"));
		//}
		
		//return null;
  }


  // --------------------------------------------------------------------
  // METHOD: getResourceString
  // --------------------------------------------------------------------
  
  /** getResourceString() will return a given resource in the form of
    * a String. This is a convenience method for Maui components which
    * need to get text resources.
    *
    */
  
  public String getResourceString(String path) throws ResourceNotFoundException
  {
		return new String(this.getResourceBytes(path));
  }


  // --------------------------------------------------------------------
  // METHOD: getResourceBytes
  // --------------------------------------------------------------------
  
  /** getResourceBytes() will return a byte[] array containing the
    * contents of file requests.
    *
    */
  
  public byte[] getResourceBytes(String path) throws ResourceNotFoundException
  {
  	int theReference = Profiler.start (MauiRuntimeEngine.SOURCE_RESOURCE,
  																		 MauiRuntimeEngine.ACTION_CREATE);
		byte[] bytes = null;
		
    // [1] The ZipFile class doesn't seem to like a leading '/' in the
    //     path, so we shall remove it if it is there.
    if (path.startsWith("/"))
    {
    	path = path.substring(1, path.length());
    }


		// [2] Check to see if the requested resource has been cached by
		//     the CacheManager. If that's the case, return the cached
		//     version.
		{
			Object cachedObject = this.getObjectFromCache(path);
			
			if (cachedObject != null)
			{
				Profiler.finish (theReference,
												 MauiRuntimeEngine.SOURCE_RESOURCE,
													MauiRuntimeEngine.ACTION_CACHE_HIT,
													path);
				return (byte[])cachedObject;
			}
		}

		// [3] Get the file from the ZIP file
		{
	    	ZipFile archive = this.getResourceArchiveForFile(path);
			bytes = this.getBytesFromZipFile(archive, path);
			try
			{
				archive.close ();
			}
			catch (IOException e)
			{
			}
		}
		
		// [4] Add this resource to the CacheManager
		{
			this.putObjectInCache(path, bytes);
		}

		Profiler.finish (theReference, path);		
		return bytes;
  }


  // --------------------------------------------------------------------
  // METHOD: getResourceBytes
  // --------------------------------------------------------------------
  
  /** getResourceBytes() will return a byte[] array containing the
    * contents of file requests.
    *
    * This method is synchronized because it's possible to have more than one concurrent request
    * for the same resource.  Without synchronization, multiple renders (and possibly messing up the
    * cache) of the same resource could occur.  But the first caller should do the render, and
    * store the results in the cache.  Subsequent callers should get the resource from the cache only.
    *
    */
  
  public byte[] getResourceBytes(ResourceDescription description) throws ResourceNotFoundException, ComponentNotPaintableException
  {
		byte[] bytes = null;
		MComponent component = null;

		int theReference = Profiler.start (MauiRuntimeEngine.SOURCE_RESOURCE,
																			 MauiRuntimeEngine.ACTION_CREATE);


		String theCacheKey = description.toString ();
		// [0] Check to see if the requested resource has been cached by
		//     the CacheManager. If that's the case, return the cached
		//     version.
		{
			Object cachedObject = this.getObjectFromCache (theCacheKey);
			
			if (cachedObject != null)
			{
				Profiler.finish (theReference,
								 MauiRuntimeEngine.SOURCE_RESOURCE,
								 MauiRuntimeEngine.ACTION_CACHE_HIT,
								 description.toString ());
				return (byte[])cachedObject;
			}
		}

		try
		{
			String componentID = description.getProperty("id");
			String path = description.getProperty("path");
			String state = description.getProperty("state");

			// [1] If there is a componentID, get the component out of the
			//     ComponentManager. Using the state of the requested image
			//     call the appropriate paintXXX() method and get an output
			//     image.
			if (componentID != null)
			{
				component = (MComponent) getCrossReference (componentID);
				// Do some quick error checking
				if (state == null)
				{
					System.err.println(new WarningString("The state parameter for component " + componentID + " was null. Setting to 'default'."));
					state = "default";
				}

				long startTime = System.currentTimeMillis();
				if (component == null)
				{
					component = ComponentManager.getInstance().getComponent(componentID);
				}

				if (component == null)
				{
					throw new ResourceNotFoundException ("The component parameter for component " + componentID + " was null.");
				}

				Image image = ImageFactory.getInstance().getImage(component.getSize());

				// [a] If the component implements the Paintable interface,
				//     paint the state of which the request is (if possible).
				//     If the component doesn't implement the interface
				//     representing the requested state, the default  paint()
				//     method will be used as specified in the Paintable
				//     interface.
				if (component instanceof Paintable)
				{
					try
					{
					  if (state.equals(MouseOutPaintable.STATE_NAME))
					  {
					  	((MouseOutPaintable)component).paintMouseOut(image.getGraphics());
					  }
					  else if (state.equals(MouseOverPaintable.STATE_NAME))
					  {
					  	((MouseOverPaintable)component).paintMouseOver(image.getGraphics());
					  }
					  else if (state.equals(MouseDownPaintable.STATE_NAME))
					  {
					  	((MouseDownPaintable)component).paintMouseDown(image.getGraphics());
					  }
					  else if (state.equals(DisabledPaintable.STATE_NAME))
					  {
					  	((DisabledPaintable)component).paintDisabled(image.getGraphics());
					  }
					  else if (state.equals (SelectedPaintable.STATE_NAME))
					  {
					  	((SelectedPaintable) component).paintSelected (image.getGraphics ());
					  }
					  else
					  {
					  	((Paintable)component).paint(image.getGraphics());
					  }
					}
					catch (ClassCastException exception)
					{
					  ((Paintable)component).paint(image.getGraphics());
					}
			  
				  bytes = ImageFactory.getInstance().getOutputImage(image, ImageFactory.GIF);
				  image.flush ();

				  System.out.println("component " + componentID + "-" + state + " took " + (System.currentTimeMillis() - startTime) + " millis to render.");
				  Profiler.finish (theReference,
				  								 description.toString ());
				}

				// [b] If the component does not implement the Paintable
				//     interface, a ComponentNotPaintableException will
				//     be thrown. Requests should not be coming in for
				//     this component, and should be handled by the catcher
				//     in an appropriate way.
				else
				{
					throw new ComponentNotPaintableException("Component " + componentID + " does not implement the Paintable interface.");
				}
			}
			
			// [2] Else, get a static image using the path property. Some
			//     images need not be generated, as they will always be the
			//     same. These typically are images such as corner pieces for
			//     buttons, windows, and other widgets.
			else
			{
				if (path == null)
				{
					throw new ResourceNotFoundException("The 'path' attribute was not set. 'Your request should look something like: /?getImage=true&path=/path/to/image.gif'.");
				}
				
			  bytes = this.getResourceBytes(path);
			}
		}
		catch (Exception exception)
		{
			if (exception instanceof ResourceNotFoundException)
			{
				throw (ResourceNotFoundException)exception;
			}
			else
			{
				System.out.println(new ErrorString(exception, "Occurred in ResourceManager.getResourceBytes()."));
				exception.printStackTrace ();
			}
		}
		
		// [3] Add this resource to the CacheManager
		{
			this.putObjectInCache (theCacheKey, bytes);
			if (component != null)
			{
				String theCrossReference = (String) getCrossReference (component);
				if (theCrossReference == null)
				{
					theCrossReference = component.getComponentID ();
				}
				Vector theCacheObjects = (Vector) cacheCrossReferences.get (theCrossReference);
				if (theCacheObjects == null)
				{
					theCacheObjects = new Vector (5);
					cacheCrossReferences.put (theCrossReference, theCacheObjects);
				}
				theCacheObjects.addElement (theCacheKey);
			}
		}
		return bytes;
  }


	// --------------------------------------------------------------------
	// METHOD: getResourceStream
	// --------------------------------------------------------------------
  
	public BufferedInputStream getResourceStream(String path)
	{
		BufferedInputStream stream = null;

		try
		{
			byte[] bytes = this.getResourceBytes(path);

			ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);

			stream = new BufferedInputStream(byteStream);
		}
		catch (Exception exception)
		{
			System.out.println(new ErrorString(exception, "ResourceManager.getResourceStream()"));
		}

		return stream;
	}
	
	// --------------------------------------------------------------------
	// METHOD: getResourceArchiveForFile
	// --------------------------------------------------------------------
  
	/** getResourceArchiveForFile() locates and returns the archive file
	  * which contains the requested resource file. If a matching archive
	  * file could not be found, as ResourceNotFoundException will be
	  * thrown.
	  *
	  */
  
	private ZipFile getResourceArchiveForFile(String path) throws ResourceNotFoundException
	{
		// [1] The resourceHash Hashtable contains a list of resources
		//     along with the corresponding archive file which holds
		//     the resource. First we will ask the resourceHash what
		//     the filename is for the containing archive. If no file-
		//     name is returned, a ResourceNotFoundException will be
		//     thrown. In the case where a match could be found, a
		//     reference to the containing ZipFile will be returned.
		//     This is done through the use of the additionalResources
		//     Hashtable which contains a list of the filenames of
		//     the archive files and references to the actual ZipFile.

		String containingArchiveName = (String)this.resourceHash.get(path);
    
		if (containingArchiveName == null)
		{
			throw new ResourceNotFoundException("The file '" + path + "' was not found in any resource archive.");
		}
		
		AdditionalResource theResource =
			(AdditionalResource) additionalResources.get (containingArchiveName);
		ZipFile retVal = null;
		
		if (theResource != null)
		{
			try
			{
				retVal = new ZipFile (theResource.getFile ());
			}
			catch (IOException e)
			{
			}
		}
    
		return retVal;
	}


	// --------------------------------------------------------------------
	// METHOD: getBytesFromZipFile
	// --------------------------------------------------------------------
  
	/** getBytesFromZipFile() returns a byte[] containing the contents of
	  * a specified entry for a specified ZipFile.
	  *
	  */
  
	private byte[] getBytesFromZipFile(ZipFile file, String path)
	{
		byte[] bytes = null;

		try
		{
			// [1] Get an entry object for the resource file we need
			ZipEntry entry = file.getEntry(path);
      
			// [2] Get an InputStream to the resource file
			BufferedInputStream input = new BufferedInputStream(file.getInputStream(entry));
      
			// [3] Create a ByteArrayOutputStream to feed all of the bytes
			//     into. We can return a byte[] from this object.
			ByteArrayOutputStream output = new ByteArrayOutputStream((int)entry.getSize());
      
			// [4] Transfer the contents of the InputStream into the
			//     ByteArrayOutputStream
			{
				byte[] buffer = new byte[128];
				int bytesRead = -1;
        
				try
				{
					while ((bytesRead = input.read(buffer, 0, 128)) != -1)
					{
						output.write(buffer, 0, bytesRead);
					}
				}
				catch (IOException exception)
        {
					System.err.println("ResourceManager.getBytesFromZipFile(): an IOException was caught while extracting the file '" + path + "' from the archive '" + file.getName() + "'.");
				}
        
				bytes = output.toByteArray();

				output.close();
				input.close();
			}
		}
		catch (Exception exception)
		{
			System.err.println(new ErrorString(exception, "ResourceManager.getBytesFromZipFile()"));
		}
    
		return bytes;
	}


	// --------------------------------------------------------------------
	// METHOD: getCacheDomain
	// --------------------------------------------------------------------
  
	/** getCacheDomain() gets a CacheDomain object for this class to use.
	  * Any object which is cached gets stored in a particular domain, in
	  * this case, one called "ResourceManager" (see this.CACHE_DOMAIN).
	  * If a CacheDomain does not exist for the ResourceManager, this
	  * method will create one and return it.
	  *
	  */
  
	private CacheDomain getCacheDomain()
	{
		CacheManager cacheManager = (CacheManager)CacheManager.getInstance();
		CacheDomain domain = (CacheDomain)cacheManager.locate(this.CACHE_DOMAIN);
  	
		// If no such domain exists, create one.
		if (domain == null)
		{
			domain = (CacheDomain)cacheManager.create(this.CACHE_DOMAIN);
		}
		
		return domain;
	}
  

	// --------------------------------------------------------------------
	// METHOD: getObjectFromCache
	// --------------------------------------------------------------------
  
	/** getObjectFromCache() gets an Object which is stored using the
	  * CacheManager. If the requested object does not exist, null will be
	  * returned.
	  *
	  */
  
	private Object getObjectFromCache(Object key)
	{
		CacheDomain domain = this.getCacheDomain();
		CacheObject cacheObject = (CacheObject)domain.locate(key);

		return (cacheObject == null ? null : cacheObject.get());
	}
  
  
	// --------------------------------------------------------------------
	// METHOD: putObjectInCache
	// --------------------------------------------------------------------
  
	/** putObjectInCache() stores an object using a given key in the Cache-
	  * Domain (supplied by the CacheManager) used by the ResourceManager.
	  *
	  */
	
	private void putObjectInCache(Object key, Object object)
	{
		CacheDomain domain = this.getCacheDomain();
		final int theHashCode = key.hashCode ();
		
		domain.create(key, object);
	}
	
	private void purgeCache (Object aCrossReference)
	{
		CacheDomain domain = getCacheDomain ();
		Vector theResourceKeys = (Vector) cacheCrossReferences.get (aCrossReference);
		if (theResourceKeys != null)
		{
			Object [] theKeyArray = theResourceKeys.toArray ();
			for (int i = 0; i < theKeyArray.length; i++)
			{
				CacheObject theResource = (CacheObject) domain.remove (theKeyArray [i]);
				if (theResource != null)
				{
					theResource.put (null);
				}
			}
			cacheCrossReferences.remove (aCrossReference);
		}
	}	

	// --------------------------------------------------------------------
}

// ======================================================================
// Copyright 2000 Bitmovers Software - All rights reserved            EOF