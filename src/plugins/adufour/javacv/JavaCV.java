package plugins.adufour.javacv;

import icy.plugin.PluginLoader;
import icy.plugin.abstract_.Plugin;
import icy.plugin.interface_.PluginLibrary;
import icy.system.IcyHandledException;
import icy.system.SystemUtil;

import java.io.InputStream;

import org.xeustechnologies.jcl.JarClassLoader;

import plugins.adufour.javacv.osx.OSXLoader;

/**
 * Icy implementation of <a href=http://code.google.com/p/javacv>JavaCV</a>, a Java wrapper for the
 * popular <a href=opencv.org>OpenCV</a> library. This class provides a
 * {@link #initialize()} method to load the OpenCV libraries into Icy, and should be called before
 * using any OpenCV-based methods
 * 
 * @author Alexandre Dufour
 * 
 */
public class JavaCV extends Plugin implements PluginLibrary
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
            initialized = true;
            
            ClassLoader cl = PluginLoader.getLoader();
            
            if (cl instanceof JarClassLoader)
            {
                JarClassLoader jcl = (JarClassLoader) cl;
                InputStream javacv = JavaCV.class.getResourceAsStream("javacv.jar");
                jcl.add(javacv);
                InputStream javacpp = JavaCV.class.getResourceAsStream("javacpp.jar");
                jcl.add(javacpp);
            }
            
            if (SystemUtil.isMac())
            {
                OSXLoader.initialize();
            }
            else throw new IcyHandledException("OpenCV for Icy currently supports Mac OS X only");
        }
    }
}
