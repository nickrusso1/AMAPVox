/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ird.voxelidar.graphics3d.object.voxelspace;

import com.jogamp.common.nio.Buffers;
import fr.ird.voxelidar.frame.JFrameSettingUp;
import fr.ird.voxelidar.graphics2d.image.ScaleGradient;
import fr.ird.voxelidar.graphics3d.mesh.Attribut;
import fr.ird.voxelidar.graphics3d.mesh.Grid;
import fr.ird.voxelidar.graphics3d.mesh.Mesh;
import fr.ird.voxelidar.graphics3d.mesh.MeshFactory;
import fr.ird.voxelidar.graphics3d.object.terrain.Terrain;
import fr.ird.voxelidar.graphics3d.shader.Shader;
import fr.ird.voxelidar.io.file.FileManager;
import fr.ird.voxelidar.math.vector.Vec3F;
import fr.ird.voxelidar.util.ColorGradient;
import fr.ird.voxelidar.util.Settings;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Float.NaN;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.media.opengl.GL3;
import javax.swing.SwingWorker;
import javax.swing.event.EventListenerList;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.log4j.Logger;

/**
 *
 * @author Julien
 */
public class VoxelSpace {
    
    final static Logger logger = Logger.getLogger(VoxelSpace.class);
    
    public static final int FLOAT_SIZE = Buffers.SIZEOF_FLOAT;
    public static final int INT_SIZE = Buffers.SIZEOF_INT;
    public static final int SHORT_SIZE = Buffers.SIZEOF_SHORT;
    
    private ArrayList<Voxel> voxelList;
    public Mesh cube;
    private float cubeSize;
    private Shader simpleShader;
    private String attributToVisualize;
    public int nX, nY, nZ;
    private float startPointX, startPointY, startPointZ;
    public float widthX, widthY, widthZ;
    private float resolution;
    private boolean fileLoaded;
    public float attributValueMax;
    public float attributValueMin;
    private float instancePositions[];
    private float instanceColors[];
    public boolean arrayLoaded = false;
    private Settings settings;
    
    private FloatBuffer instancePositionsBuffer;
    private FloatBuffer instanceColorsBuffer;   
    
    private int vboId, vaoId, iboId;
    private int gridVaoId;
    private int gridVboId;
    private int gridIboId;
    private Grid grid;
    
    public float centerX;
    public float centerY;
    public float centerZ;
    
    private boolean updateValue;
    private Attribut attribut;
    
    private Color[] gradient = ColorGradient.GRADIENT_HEAT;
    
    private final int shaderId;
    private boolean gradientUpdated = false;
    private boolean cubeSizeUpdated;
    
    private int readFileProgress; 
    
    private final EventListenerList listeners;

    public void setReadFileProgress(int progress) {
        this.readFileProgress = progress;
        fireReadFileProgress(progress);
    }
    
    public void fireReadFileProgress(int progress){
        
        for(VoxelSpaceListener listener :listeners.getListeners(VoxelSpaceListener.class)){
            
            listener.voxelSpaceCreationProgress(progress);
        }
    }

    public void setFileLoaded(boolean fileLoaded) {
        this.fileLoaded = fileLoaded;
        
        if(fileLoaded){
            firefileLoaded();
        }
    }

    public boolean isFileLoaded() {
        return fileLoaded;
    }

    public Color[] getGradient() {
        return gradient;
    }
    
    public void firefileLoaded(){
        
        for(VoxelSpaceListener listener :listeners.getListeners(VoxelSpaceListener.class)){
            
            listener.voxelSpaceCreationFinished();
        }
    }
    
    public void addVoxelSpaceListener(VoxelSpaceListener listener){
        listeners.add(VoxelSpaceListener.class, listener);
    }
    
    public int getShaderId() {
        return shaderId;
    }
    
    public File file;

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public float getCenterZ() {
        return centerZ;
    }

    public ArrayList<Voxel> getVoxelList() {
        return voxelList;
    }

    public boolean isGradientUpdated() {
        return gradientUpdated;
    }

    public void setAttributToVisualize(String attributToVisualize) {
        this.attributToVisualize = attributToVisualize;
    }
    
    public VoxelSpace(Settings settings){
        
        voxelList = new ArrayList<>();
        listeners = new EventListenerList();
        fileLoaded = false;
        this.shaderId = 0;
    }
    
    public VoxelSpace(GL3 gl, int shaderId, Settings settings){
        
        voxelList = new ArrayList<>();
        listeners = new EventListenerList();
        fileLoaded = false;
        this.shaderId = shaderId;
        this.settings = settings;
    }
    
    private void setMetadata(String metadataLine){
        
        String[] metadata = metadataLine.split(" ");

        nX = Integer.valueOf(metadata[0]);
        nY = Integer.valueOf(metadata[1]);
        nZ = Integer.valueOf(metadata[2]);
        resolution = Float.valueOf(metadata[3]);

        startPointX = Float.valueOf(metadata[4]);
        startPointY = Float.valueOf(metadata[5]);
        startPointZ = Float.valueOf(metadata[6]);
    }
    
    private void setWidth(){
        
        if(voxelList.size() > 0){
            
            widthX = (voxelList.get(voxelList.size()-1).x) - (voxelList.get(0).x);
            widthY = (voxelList.get(voxelList.size()-1).y) - (voxelList.get(0).y);
            widthZ = (voxelList.get(voxelList.size()-1).z) - (voxelList.get(0).z);
        }
    }
    
    private void setCenter(){
        
        if(voxelList.size() > 0){
            
            centerX = ((voxelList.get(voxelList.size()-1).x) - (voxelList.get(0).x))/2.0f;
            centerY = ((voxelList.get(voxelList.size()-1).y) - (voxelList.get(0).y))/2.0f;
            centerZ = ((voxelList.get(voxelList.size()-1).z) - (voxelList.get(0).z))/2.0f;
        }
    }
    
    private boolean isVoxelSpaceFile(File f){
        
        String header = FileManager.readHeader(f.getAbsolutePath());
        
        return header.equals("VOXEL SPACE");
    }
    
    public void loadFromFile(File f) throws Exception{
        
        if(isVoxelSpaceFile(f)){
            
            setFileLoaded(false);
        
            this.file =f;

            //final JProgressLoadingFile progress = new JProgressLoadingFile(parent);
            //progress.setVisible(true);
            SwingWorker sw = new SwingWorker() {


                @Override
                protected Object doInBackground() {

                    try {

                        int count = FileManager.getLineNumber(file.getAbsolutePath());

                        /******read file*****/

                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String parameters[] = reader.readLine().split(" ");

                        //metadata
                        setMetadata(reader.readLine());

                        //first values line
                        String line = reader.readLine();

                        int lineNumber = 0;

                        while (line != null) {

                            String[] attributs = line.split(" ");

                            //create voxel
                            if(!updateValue){

                                int indiceX = Integer.valueOf(attributs[0]);
                                int indiceZ = Integer.valueOf(attributs[1]);
                                int indiceY = Integer.valueOf(attributs[2]);

                                Map<String,Float> mapAttributs = new HashMap<>();

                                for (int i=0;i<attributs.length;i++) {
                                    mapAttributs.put(parameters[i], Float.valueOf(attributs[i]));
                                }

                                float posX = indiceX+startPointX-(resolution/2.0f);
                                float posY = indiceY+startPointY-(resolution/2.0f);
                                float posZ = indiceZ+startPointZ-(resolution/2.0f);

                                voxelList.add(new Voxel(indiceX, indiceY, indiceZ, posX, posY, posZ, mapAttributs, 1.0f));

                            }

                            line = reader.readLine();

                            lineNumber++;

                            setReadFileProgress((lineNumber * 100) / count);
                        }

                        reader.close();

                        setFileLoaded(true);

                        setCenter();
                        setWidth();

                    } catch (FileNotFoundException ex) {
                        logger.error("cannot load voxel space from file", ex);
                    } catch (IOException ex) {
                        logger.error("cannot load voxel space from file", ex);
                    }

                    return null;

                }
            };

            sw.execute();
        }else{
            throw new Exception("Not a voxel space file (header must be VOXEL");
        }     
    }
    
    public void loadFromFile(File f, Map<String,Attribut> mapAttributs, Terrain ground,boolean update){
        
        setFileLoaded(false);
        
        this.updateValue= update;
        
        attribut = mapAttributs.get(attributToVisualize);
        final Terrain terrain = ground;
        
        this.file =f;
        
        SwingWorker sw = new SwingWorker() {
            
            
            @Override
            protected Object doInBackground() {

                /******count line number*****/
                int compteur = 0;
                MultiKeyMap mapTerrainXY = null;
                
                if(terrain !=null){
                    
                    mapTerrainXY = terrain.getXYStructure();
                }
                
                
                try {
                    
                    /****count line number****/
                    int count = FileManager.getLineNumber(file.getAbsolutePath());

                    /******read file*****/
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String parametersLine[] = reader.readLine().split(" ");
                    
                    
                    //metadata
                    setMetadata(reader.readLine());
                    
                    //first values line
                    String line = reader.readLine();
                    
                    int lineNumber = 0;
                    
                    boolean valMinMaxInit = false;

                    while (line != null) {
                        
                        String[] attributs = line.split(" ");
                        
                        for(int i=0;i<parametersLine.length;i++){
                            attribut.getExpression().setVariable(parametersLine[i], Float.valueOf(attributs[i]));
                        }
                        
                        float attributValue =0;
                        
                        try{
                            attributValue = (float) attribut.getExpression().evaluate();
                        }catch(Exception e){
                            attributValue = NaN;
                        }
                        
                        
                        //initialize minimum and maximum attributs values
                        if(lineNumber >= 0 && !valMinMaxInit && attributValue != 0){

                            attributValueMax = attributValue;
                            attributValueMin = attributValue;

                            valMinMaxInit = true;
                        }
                        
                        //edit voxel value
                        if(updateValue && voxelList.size() > 0){
                            voxelList.get(compteur).attributValue = attributValue;
                            
                        }
                        
                        //set maximum attribut value
                        if(attributValue>attributValueMax){

                            attributValueMax = attributValue;
                        }

                        //set minimum attribut value
                        if(attributValue < attributValueMin){

                            attributValueMin = attributValue;
                        }
                        
                        Voxel voxel;
                                
                        //create voxel
                        if(!updateValue){
                            
                            int indiceX = Integer.valueOf(attributs[0]);
                            int indiceZ = Integer.valueOf(attributs[1]);
                            int indiceY = Integer.valueOf(attributs[2]);
                            
                            float posX = indiceX+startPointX-(resolution/2.0f);
                            float posY = indiceY+startPointZ-(resolution/2.0f);
                            float posZ = indiceZ+startPointY-(resolution/2.0f);
                            
                            voxel = new Voxel(indiceX, indiceY, indiceZ, posX, posY, posZ, attributValue);
                        }else{
                            voxel = voxelList.get(compteur);
                        }
                        
                        boolean drawVoxel;

                        drawVoxel = !(Float.isNaN(voxel.attributValue) || voxel.attributValue == -1.0f || (!settings.drawNullVoxel && voxel.attributValue == 0));

                        //if a terrain was loaded
                        if(mapTerrainXY != null){

                            float hauteurTerrain = 0;
                            try{
                                hauteurTerrain = (float) mapTerrainXY.get(voxel.x, voxel.z);
                            }catch(Exception e){
                                logger.error(null, e);
                            }

                            if((voxel.y<hauteurTerrain) && !settings.drawVoxelUnderground ){
                                drawVoxel = false;
                            }
                        }

                        if(drawVoxel){
                            voxel.alpha = 1.0f;
                        }else{
                            voxel.alpha = 0.0f;
                        }
                        
                        if(!updateValue){
                            voxelList.add(voxel);
                        }else{
                            compteur++;
                        }

                        line = reader.readLine();
                        
                        lineNumber++;
                        
                        setReadFileProgress((lineNumber * 100) / count);
                    }
                    
                    reader.close();
                    
                    setGradientColor(gradient);
                    
                    if(updateValue){
                        
                        updateInstanceColorBuffer();
                    }
                    
                    setCenter();
                    setWidth();
                    
                    setFileLoaded(true);

                } catch (FileNotFoundException ex) {
                    logger.error(null, ex);
                } catch (IOException ex) {
                    logger.error(null, ex);
                }

                return null;

            }
        };

        sw.execute();
    }
    
    public void setGradientColor(Color[] gradientColor){
        
        this.gradient = gradientColor;
        
        ColorGradient color = new ColorGradient(attributValueMin, attributValueMax);
        color.setGradientColor(gradientColor);

        for (int i=0;i<voxelList.size();i++){

            Color colorGenerated = color.getColor(voxelList.get(i).attributValue);

            if(voxelList.get(i).alpha == 0){
                voxelList.get(i).color = new Vec3F(colorGenerated.getRed()/255.0f, colorGenerated.getGreen()/255.0f, colorGenerated.getBlue()/255.0f);
            }else{
                voxelList.get(i).color = new Vec3F(colorGenerated.getRed()/255.0f, colorGenerated.getGreen()/255.0f, colorGenerated.getBlue()/255.0f);
            }
        }
        
        //voxelList = ImageEqualisation.scaleHistogramm(voxelList);
        //voxelList = ImageEqualisation.voxelSpaceEqualisation(voxelList);
        
        
    }
    
    public void updateInstanceColorBuffer(){
        
        gradientUpdated = false;
        
    }
    
    public BufferedImage createScaleImage(int width, int height){
        
        return ScaleGradient.generateScale(gradient, attributValueMin, attributValueMax, width, height);
    }
    
    public void updateCubeSize(GL3 gl, float size){
        
        cubeSize = size;
        cubeSizeUpdated = false;
    }
    
    public void initBuffer(GL3 gl, Shader shader){
        
        cubeSize = 0.5f;
        cube = MeshFactory.createCube(cubeSize);
        
        instancePositions = new float[voxelList.size()*3];
        instanceColors = new float[voxelList.size()*4];

        for (int i=0, j=0, k=0;i<voxelList.size();i++, j+=3 ,k+=4) {

            instancePositions[j] = voxelList.get(i).x;
            instancePositions[j+1] = voxelList.get(i).y;
            instancePositions[j+2] = voxelList.get(i).z;

            instanceColors[k] = voxelList.get(i).color.x;
            instanceColors[k+1] = voxelList.get(i).color.y;
            instanceColors[k+2] = voxelList.get(i).color.z;
            instanceColors[k+3] = voxelList.get(i).alpha;
        }
        
        instancePositionsBuffer = Buffers.newDirectFloatBuffer(instancePositions);
        instanceColorsBuffer = Buffers.newDirectFloatBuffer(instanceColors);
        
        
        //generate vbo and ibo buffers
        IntBuffer tmp = IntBuffer.allocate(2);
        gl.glGenBuffers(2, tmp);
        vboId=tmp.get(0);
        iboId=tmp.get(1);
        
        
        
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vboId);
        
            //allocate total memory
            gl.glBufferData(GL3.GL_ARRAY_BUFFER, (cube.vertexBuffer.capacity()*FLOAT_SIZE)+(instancePositionsBuffer.capacity()*FLOAT_SIZE)+(instanceColorsBuffer.capacity()*FLOAT_SIZE), null, GL3.GL_STATIC_DRAW);
            
            /***set buffers in global buffer (int target, long offset, long size, Buffer buffer)****/
            
            //set vertex buffer
            gl.glBufferSubData(GL3.GL_ARRAY_BUFFER, 0, cube.vertexBuffer.capacity()*FLOAT_SIZE, cube.vertexBuffer);
            
            //set instances positions buffer
            gl.glBufferSubData(GL3.GL_ARRAY_BUFFER, cube.vertexBuffer.capacity()*FLOAT_SIZE, instancePositionsBuffer.capacity()*FLOAT_SIZE, instancePositionsBuffer);
            
            //set instances colors buffer
            gl.glBufferSubData(GL3.GL_ARRAY_BUFFER, (cube.vertexBuffer.capacity()+instancePositionsBuffer.capacity())*FLOAT_SIZE, instanceColorsBuffer.capacity()*FLOAT_SIZE, instanceColorsBuffer);
            
            gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, iboId);
                gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, cube.indexBuffer.capacity()*SHORT_SIZE, cube.indexBuffer, GL3.GL_STATIC_DRAW);
            gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, 0);
        
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
        
        //generate vao
        int[] tmp2 = new int[1];
        gl.glGenVertexArrays(1, tmp2, 0);
        vaoId = tmp2[0];
        
        gl.glBindVertexArray(vaoId);
        
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vboId);

                gl.glEnableVertexAttribArray(shader.attributeMap.get("position"));
                gl.glVertexAttribPointer(shader.attributeMap.get("position"), 3, GL3.GL_FLOAT, false, 0, 0);
                
                gl.glEnableVertexAttribArray(shader.attributeMap.get("instance_position"));
                gl.glVertexAttribPointer(shader.attributeMap.get("instance_position"), 3, GL3.GL_FLOAT, false, 0, cube.vertexBuffer.capacity()*FLOAT_SIZE);
                gl.glVertexAttribDivisor(shader.attributeMap.get("instance_position"), 1);
                
                gl.glEnableVertexAttribArray(shader.attributeMap.get("instance_color"));
                gl.glVertexAttribPointer(shader.attributeMap.get("instance_color"), 4, GL3.GL_FLOAT, false, 0, (cube.vertexBuffer.capacity()+instancePositionsBuffer.capacity())*FLOAT_SIZE);
                gl.glVertexAttribDivisor(shader.attributeMap.get("instance_color"), 1);
                 
            gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, iboId);
            
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
            
        gl.glBindVertexArray(0);
        
        gradientUpdated = true;
    }
    
    public void render(GL3 gl){
        

        //draw voxels
        gl.glBindVertexArray(vaoId);
            gl.glDrawElementsInstanced(GL3.GL_TRIANGLES, cube.vertexCount, GL3.GL_UNSIGNED_SHORT, 0, voxelList.size());
        gl.glBindVertexArray(0);
        
        if(!gradientUpdated){
            
            instanceColors = new float[voxelList.size()*4];

            for (int i=0, j=0;i<voxelList.size();i++, j+=4) {

                instanceColors[j] = voxelList.get(i).color.x;
                instanceColors[j+1] = voxelList.get(i).color.y;
                instanceColors[j+2] = voxelList.get(i).color.z;
                instanceColors[j+3] = voxelList.get(i).alpha;
            }

            instanceColorsBuffer = Buffers.newDirectFloatBuffer(instanceColors);
   
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vboId);

                gl.glBufferSubData(GL3.GL_ARRAY_BUFFER, (cube.vertexBuffer.capacity()+instancePositionsBuffer.capacity())*FLOAT_SIZE, instanceColorsBuffer.capacity()*FLOAT_SIZE, instanceColorsBuffer);

            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
            
            gradientUpdated = true;
        }
        
        if(!cubeSizeUpdated){
            
            cube = MeshFactory.createCube(cubeSize);
        
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vboId);

                gl.glBufferSubData(GL3.GL_ARRAY_BUFFER, 0, cube.vertexBuffer.capacity()*FLOAT_SIZE, cube.vertexBuffer);

            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
            
            cubeSizeUpdated = true;
        }               
    }
}
