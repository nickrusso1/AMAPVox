package fr.amap.lidar.amapvox.voxviewer;

import com.jogamp.nativewindow.util.Point;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowUpdateEvent;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;
import fr.amap.lidar.amapvox.voxviewer.event.EventManager;
import fr.amap.lidar.amapvox.voxviewer.input.InputKeyListener;
import fr.amap.lidar.amapvox.voxviewer.input.InputMouseAdapter;
import fr.amap.lidar.amapvox.voxviewer.renderer.GLRenderFrame;
import fr.amap.lidar.amapvox.voxviewer.renderer.MinimalWindowAdapter;
import fr.amap.lidar.amapvox.voxviewer.renderer.JoglListener;
import fr.amap.lidar.amapvox.voxviewer.renderer.MinimalKeyAdapter;
import fr.amap.lidar.amapvox.voxviewer.renderer.MinimalMouseAdapter;
import java.io.File;
import java.util.List;

public class Viewer3D {

    private File voxelFile;
    private String attributeToView;
    static double SCREEN_WIDTH;
    static double SCREEN_HEIGHT;
    private List<String> parameters;
    private final GLRenderFrame renderFrame;
    private final JoglListener joglContext;
    private final FPSAnimator animator;  
    
    private boolean focused;
    
    private int width;
    private int height;
    
    private boolean dynamicDraw = false;
    private MinimalMouseAdapter minimalMouseAdapter;
    
    public Viewer3D(int posX, int posY, int width, int height, String title) throws Exception{
        
        try{
            
            GLProfile glp = GLProfile.getGL2GL3();
            GLCapabilities caps = new GLCapabilities(glp);
            caps.setDoubleBuffered(true);
            
            this.width = width;
            this.height = height;
            
            renderFrame = new GLRenderFrame(caps, posX, posY, width, height, title);
            //renderFrame = GLRenderFrame.create(caps, posX, posY, width, height, title);
            animator = new FPSAnimator(60);
            
            /* From doc : 
            An Animator can be attached to one or more GLAutoDrawables to drive their display() methods in a loop.
            The Animator class creates a background thread in which the calls to display() are performed.
            After each drawable has been redrawn, a brief pause is performed to avoid swamping the CPU,
            unless setRunAsFastAsPossible(boolean) has been called.
            */
            animator.add(renderFrame);

            joglContext = new JoglListener(animator);
            joglContext.getScene().setWidth(width);
            joglContext.getScene().setHeight(height);
            
            renderFrame.addGLEventListener(joglContext);
            renderFrame.addWindowListener(new MinimalWindowAdapter(animator));
            
            
            //basic input adapters for waking up animator if necessary
            renderFrame.addKeyListener(new MinimalKeyAdapter(animator));
            
            minimalMouseAdapter = new MinimalMouseAdapter(animator, dynamicDraw);
            renderFrame.addMouseListener(minimalMouseAdapter);      
            
            animator.start();
            
            
            
        }catch(GLException e){
            throw new GLException("Cannot init opengl", e);
        }catch(Exception e){
            throw new Exception("Unknown error happened", e);
        }
    }
    
//    public Viewer3D(int width, int height, File voxelFile, String attributeToView, 
//            boolean drawDTM, File dtmFile, boolean transformDtm, Mat4D dtmTransform, boolean fitDTMToVoxelSpace, int fittingMargin){
//        
//        String cmd = "--width=" + width+","+
//                     "--height="+height+","+
//                     "--input="+voxelFile.getAbsolutePath()+","+
//                     "--attribut="+attributeToView+",";
//        
//        if(drawDTM && dtmFile != null){
//            cmd += "--dtm="+dtmFile.getAbsolutePath()+",";
//            
//            if(transformDtm){
//                cmd += "--dtm-transform"+",";
//            }
//            
//            if(dtmTransform == null){
//                dtmTransform = Mat4D.identity();
//            }
//            cmd += "--dtm-transf-matrix="+dtmTransform.toString()+",";
//            
//            if(fitDTMToVoxelSpace){
//                cmd += "--dtm-fit"+",";
//                cmd += "--dtm-fitting-margin="+fittingMargin+",";
//            }
//        }
//        
//        
//        
//        cmd = cmd.substring(0, cmd.length()-1);
//                     
//        //System.out.println(cmd.replaceAll(",", " "));
//        
//        String[] split = cmd.split(",");
//        parameters = new ArrayList<>(split.length);
//        
//        for(String s : split){
//            parameters.add(s);
//        }
//        
//        //launch(cmd.split(","));
//    }
    
    
    
    private static void usage(){
        
        System.out.println("Utilisation : java -jar VoxViewer.jar [PARAMETRES]\n");
        System.out.println("Parametre\tDescription\n");
        System.out.println("--help\tAffiche ce message");
        System.out.println("--input=<Fichier voxel>\t\tChemin vers le fichier voxel (obligatoire)");
        System.out.println("--attribut=<Nom de l'attribut>\tChemin vers le fichier voxel");
        System.out.println("--dtm=<Fichier MNT>\t\tChemin vers le modele numerique de terrain associé au modèle");
        System.out.println("--dtm-transform\t\t\tChemin vers le modele numerique de terrain associé au modèle");
        System.out.println("--dtm-fit\t\t\tCharge uniquement la partie du MNT associee a l'espace voxel");
        System.out.println("--dtm-transf-matrix=<Matrice>\tTransforme le MNT dans la vue 3D en translation et rotation,\n"
                + "\t\t\t\tla valeur de Matrice doit être une liste de 16 valeurs séparées par des virgules.\n"
                + "\t\t\t\tLa matrice s'écrit de gauche à droite et de haut en bas.");
    }
    
    public fr.amap.lidar.amapvox.voxviewer.object.scene.Scene getScene(){
        return getJoglContext().getScene();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //Application.launch(FXViewer3D.class, "--width=853", "--height=512", "--input=/home/calcul/Documents/Julien/Sortie voxels/ALS/Paracou/2013/dalle9/las_2m.vox" ,"--attribut=PadBVTotal");
        //Application.launch(FXViewer3D.class, "--width=853", "--height=512", "--input=/home/calcul/Documents/Julien/samples_transect_sud_paracou_2013_ALS/las.vox" ,"--attribut=PadBVTotal");
        
    }
    
    
    public void attachEventManager(EventManager eventManager){
        
        joglContext.attachEventListener(eventManager);
        renderFrame.addKeyListener(new InputKeyListener(eventManager));
        renderFrame.addMouseListener(new InputMouseAdapter(eventManager));
    }
    
    public void addWindowListener(WindowListener listener){
        
        renderFrame.addWindowListener(listener);
    }

    public GLRenderFrame getRenderFrame() {
        return renderFrame;
    }
    
    
    public Point getPosition(){
        Point locationOnScreen = renderFrame.getLocationOnScreen(null);
        return new Point(locationOnScreen.getX(), locationOnScreen.getY());
    }
    
    public void show(){
        this.setOnTop();
        renderFrame.setVisible(true);
    }
    
    public void setOnTop(){
        renderFrame.setAlwaysOnTop(true);
        renderFrame.setAlwaysOnTop(false);
    }

    public JoglListener getJoglContext() {
        return joglContext;
    }

    public FPSAnimator getAnimator() {
        return animator;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setIsFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean isDynamicDraw() {
        return dynamicDraw;
    }

    public void setDynamicDraw(boolean dynamicDraw) {
        this.dynamicDraw = dynamicDraw;
        joglContext.setDynamicDraw(dynamicDraw);
        minimalMouseAdapter.setDynamicDraw(dynamicDraw);
    }
    

}
