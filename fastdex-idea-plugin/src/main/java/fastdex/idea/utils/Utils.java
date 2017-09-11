package fastdex.idea.utils;

import com.android.ddmlib.AndroidDebugBridge;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.android.actions.AndroidEnableAdbServiceAction;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.sdk.AndroidSdkUtils;
import org.jetbrains.jps.android.model.impl.JpsAndroidModuleProperties;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by pengwei on 16/9/11.
 */
public final class Utils {

    public static final String BREAK_LINE = System.getProperty("line.separator");

    public static boolean notEmpty(String text) {
        return (text != null && text.trim().length() != 0);
    }

    /**
     * 打开浏览器
     *
     * @param url
     */
    public static void openUrl(String url) {
        if (SystemInfo.isWindows) {
            try {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                URI uri = new URI(url);
                Desktop desktop = null;
                if (Desktop.isDesktopSupported()) {
                    desktop = Desktop.getDesktop();
                }
                if (desktop != null)
                    desktop.browse(uri);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 模拟键盘输入
     * @param r
     * @param key
     */
    public static void keyPressWithCtrl(Robot r, int key) {
        if (r == null) {
            return;
        }
        r.keyPress(KeyEvent.VK_CONTROL);
        r.keyPress(key);
        r.keyRelease(key);
        r.keyRelease(KeyEvent.VK_CONTROL);
        r.delay(100);
    }

    public static String[] getFastdexRunShell(Project project) {
        return new String[]{"sh","gradlew", "fastdex"};
    }

    public static String getBuildVariantName(Project project) {
        //see com.android.tools.idea.gradle.variant.view.BuildVariantView;  line:232
        for (Module module : GradleUtil.getGradleModulesWithAndroidProjects(project)) {
            AndroidFacet androidFacet = AndroidFacet.getInstance(module);
            JpsAndroidModuleProperties facetProperties = androidFacet.getProperties();
            String variantName = facetProperties.SELECTED_BUILD_VARIANT;
            if (variantName != null && variantName.length() > 1) {
                return String.valueOf(variantName.charAt(0)).toUpperCase() + variantName.substring(1);
            }
        }

        return "Debug";
    }

    public static String getBuildVariantName(Module module) {
        //see com.android.tools.idea.gradle.variant.view.BuildVariantView;  line:232

        AndroidFacet androidFacet = AndroidFacet.getInstance(module);
        JpsAndroidModuleProperties facetProperties = androidFacet.getProperties();
        String variantName = facetProperties.SELECTED_BUILD_VARIANT;
        return variantName;
    }

    public static boolean hasInitAndroidDebugBridge() {
        try {
            Field field = AndroidDebugBridge.class.getDeclaredField("sInitialized");
            field.setAccessible(true);
            Boolean val = (Boolean) field.get(null);
            if (val != null && val == Boolean.TRUE) {
               return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    public static String formatPath(String path) {
        if (!SystemInfo.isWindows) {
            return path.replaceAll(" ","\\\\ ");
        }
        return path;
    }
}
