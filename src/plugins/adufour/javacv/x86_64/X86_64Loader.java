package plugins.adufour.javacv.x86_64;

import icy.plugin.PluginLoader;
import icy.system.IcyHandledException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xeustechnologies.jcl.JarClassLoader;

public class X86_64Loader
{
    private static volatile boolean initialized = false;
    
    public static void initialize()
    {
        if (!initialized)
        {
            initialized = true;
            
            ClassLoader cl = PluginLoader.getLoader();
            
            if (cl instanceof JarClassLoader)
            {
                JarClassLoader jcl = (JarClassLoader) cl;
                System.out.print("Loading JavaCV v.2.4.3...");
                InputStream javacv = X86_64Loader.class.getResourceAsStream("javacv_x86_2.4.3.jar");
                jcl.add(javacv);
                System.out.println("done.");
            }
            else
            {
                throw new IcyHandledException("Unable to load JavaCV v.2.4.3 into the class loader");
            }
            
            try
            {
                String basePath = new File("").getAbsolutePath() + "/";
                
                // WARNING ! Loading must be done IN ORDER
                
                ArrayList<String> libs = new ArrayList<String>();
                libs.add("opencv_core243.dll");
                libs.add("opencv_imgproc243.dll");
                libs.add("opencv_highgui243.dll");
                libs.add("opencv_flann243.dll");
                libs.add("opencv_features2d243.dll");
                libs.add("opencv_ml243.dll");
                libs.add("opencv_nonfree243.dll");
                libs.add("opencv_objdetect243.dll");
                libs.add("opencv_photo243.dll");
                libs.add("opencv_video243.dll");
                libs.add("opencv_calib3d243.dll");
                // libs.add("opencv_gpu243.dll");
                libs.add("opencv_stitching243.dll");
                libs.add("opencv_legacy243.dll");
                libs.add("opencv_videostab243.dll");
                // libs.add("opencv_ts243.dll");
                // libs.add("opencv_ffmpeg243_64.dll");
                libs.add("opencv_contrib243.dll");
                libs.add("jniopencv_core.dll");
                libs.add("jniopencv_imgproc.dll");
                libs.add("jniopencv_highgui.dll");
                libs.add("jniopencv_flann.dll");
                libs.add("jniopencv_features2d.dll");
                libs.add("jniopencv_ml.dll");
                libs.add("jniopencv_nonfree.dll");
                libs.add("jniopencv_objdetect.dll");
                libs.add("jniopencv_photo.dll");
                libs.add("jniopencv_video.dll");
                libs.add("jniopencv_calib3d.dll");
                libs.add("jniopencv_stitching.dll");
                libs.add("jniopencv_legacy.dll");
                libs.add("jniopencv_videostab.dll");
                libs.add("jniopencv_contrib.dll");
                for (String name : libs)
                {
                    System.out.print("loading " + name + "...");
                    InputStream stream = X86_64Loader.class.getResourceAsStream(name);
                    File f = new File(basePath + name);
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    byte[] data = new byte[stream.available()];
                    int n;
                    while ((n = stream.read(data)) > 0)
                        fo.write(data, 0, n);
                    fo.close();
                    System.load(f.getAbsolutePath());
                    f.deleteOnExit();
                    System.out.println("done.");
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
}
