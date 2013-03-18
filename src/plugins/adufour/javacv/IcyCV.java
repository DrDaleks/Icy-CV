package plugins.adufour.javacv;

import icy.plugin.PluginLoader;
import icy.plugin.abstract_.Plugin;
import icy.plugin.classloader.JarClassLoader;
import icy.plugin.classloader.JarResources;
import icy.plugin.interface_.PluginLibrary;
import icy.system.IcyHandledException;
import icy.system.SystemUtil;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import plugins.adufour.javacv.osx.OSXLoader;
import plugins.adufour.javacv.x86.X86Loader;
import plugins.adufour.javacv.x86_64.X86_64Loader;

/**
 * Icy implementation of <a href=http://code.google.com/p/javacv>JavaCV</a>, a
 * Java wrapper for the popular <a href=opencv.org>OpenCV</a> library. This
 * class provides a {@link #initialize()} method to load the OpenCV libraries
 * into Icy, and should be called before using any OpenCV-based methods
 * 
 * @author Alexandre Dufour
 * 
 */
public class IcyCV extends Plugin implements PluginLibrary
{
	private static volatile boolean initialized = false;

	/**
	 * Loads OpenCV libraries into the system. This method can be called several
	 * times, however only the first call will actually load the libraries
	 */
	public static void initialize()
	{
		if (!initialized)
		{
			ClassLoader cl = PluginLoader.getLoader();

			Logger logger = Logger.getLogger(JarResources.class.getName());
			logger.setLevel(Level.FINEST);
			logger.addHandler(new Handler()
			{
				
				@Override
				public void publish(LogRecord arg0)
				{
					System.out.println("LOG: " + arg0.getMessage());
				}
				
				@Override
				public void flush()
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void close() throws SecurityException
				{
					// TODO Auto-generated method stub
					
				}
			});
			
			if (cl instanceof JarClassLoader)
			{
				JarClassLoader jcl = (JarClassLoader) cl;
				System.out.println("Loading JavaCPP v.0.3...");
				// InputStream javacpp =
				// IcyCV.class.getResourceAsStream("javacpp-0.3.jar");
				// jcl.add(javacpp);
				jcl.add(IcyCV.class.getResource("javacpp-0.3.jar"));
				//jcl.add("javacpp-0.3.jar");
			} else if (cl instanceof URLClassLoader)
			{
				// Probably running from an IDE, hopefully JavaCPP is already in
				// the classpath
			} else
			{
				throw new RuntimeException(
						"Unable to load JavaCPP 0.3 into the class loader");
			}

			if (SystemUtil.isMac())
			{
				OSXLoader.initialize();
			} else if (SystemUtil.isWindow())
			{
				if (SystemUtil.is32bits())
				{
					X86Loader.initialize();
				} else
				{
					X86_64Loader.initialize();
				}
			} else
				throw new IcyHandledException(
						"OpenCV is not supported on this platform");

			initialized = true;
		}
	}
}
