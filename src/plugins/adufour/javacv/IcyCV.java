package plugins.adufour.javacv;

import icy.plugin.PluginLoader;
import icy.plugin.abstract_.Plugin;
import icy.plugin.interface_.PluginLibrary;
import icy.system.IcyHandledException;
import icy.system.SystemUtil;

import java.io.InputStream;
import java.net.URLClassLoader;

import org.xeustechnologies.jcl.JarClassLoader;

import plugins.adufour.javacv.osx.OSXLoader;
import plugins.adufour.javacv.x86.X86Loader;
import plugins.adufour.javacv.x86_64.X86_64Loader;

/**
 * Icy implementation of <a href=http://code.google.com/p/javacv>JavaCV</a>, a Java wrapper for the
 * popular <a href=opencv.org>OpenCV</a> library. This class provides a {@link #initialize()} method
 * to load the OpenCV libraries into Icy, and should be called before using any OpenCV-based methods
 * 
 * @author Alexandre Dufour
 * 
 */
public class IcyCV extends Plugin implements PluginLibrary
{
    private static volatile boolean initialized = false;
    
    /**
     * Loads OpenCV libraries into the system. This method can be called several times, however only
     * the first call will actually load the libraries
     */
    public static void initialize()
    {
        if (!initialized)
        {
            ClassLoader cl = PluginLoader.getLoader();
            
            if (cl instanceof JarClassLoader)
            {
                JarClassLoader jcl = (JarClassLoader) cl;
                System.out.println("Loading JavaCPP v.0.3...");
                InputStream javacpp = IcyCV.class.getResourceAsStream("javacpp-0.3.jar");
                jcl.add(javacpp);
            }
            else if (cl instanceof URLClassLoader)
            {
                // Probably running from an IDE, hopefully JavaCPP is already in the classpath
            }
            else
            {
                throw new RuntimeException("Unable to load JavaCPP 0.3 into the class loader");
            }
            
            if (SystemUtil.isMac())
            {
                OSXLoader.initialize();
            }
            else if (SystemUtil.isWindow())
            {
                if (SystemUtil.is32bits())
                {
                    X86Loader.initialize();
                }
                else
                {
                    X86_64Loader.initialize();
                }
            }
            else throw new IcyHandledException("OpenCV is not supported on this platform");
            
            initialized = true;
        }
    }
}
