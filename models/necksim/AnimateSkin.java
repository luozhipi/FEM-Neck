package artisynth.models.necksim;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import javax.media.opengl.GL;
import maspack.geometry.PolygonalMesh;
import maspack.matrix.RigidTransform3d;
import maspack.render.GLRenderer;
import maspack.render.GLViewer;
import maspack.render.RenderProps;
import artisynth.core.driver.Main;
import artisynth.core.mechmodels.MechModel;
import artisynth.core.modelbase.RenderableModelBase;
import artisynth.core.util.ArtisynthPath;

public class AnimateSkin extends RenderableModelBase{
   
   int first = 1000;
   int last = 3700;
   String meshName = "C:/work/result/test11/processed_result_"+first+".obj";
   Reader meshReader;
   public PolygonalMesh mesh = new PolygonalMesh();
   public AnimateSkin() throws Exception
   {
      this(null, null);
   }
   public AnimateSkin(String name) throws Exception
   {
      this(name, null);
   }
   public AnimateSkin(String name, MechModel myMechModel) throws Exception
   {
      super(name);
      meshReader = new BufferedReader ( new FileReader (meshName));
      mesh.read(meshReader);
      mesh.setFixed(false);
      myRenderProps = RenderProps.createMeshProps(null);
      myRenderProps.setFaceColor(new Color(1f, 0.8f, 0.6f));
      myRenderProps.setAlpha (1);
      myRenderProps.setFaceStyle (RenderProps.Faces.FRONT);
      myRenderProps.setShading (RenderProps.Shading.GOURARD); 
      myRenderProps.setDrawEdges (false);
      Animator myAnimator = new Animator();
      myAnimator.start();
   }

   @Override
   public void render (GLRenderer renderer) {
      // TODO Auto-generated method stub
      GL gl = renderer.getGL();
      gl.glPushMatrix();
      RigidTransform3d X = new RigidTransform3d();
      GLViewer.mulTransform (gl, X);
      renderer.drawMesh (myRenderProps, mesh, /*flags=*/0);
      gl.glPopMatrix();
   }

   @Override
   public void advance (long t0, long t1) {
      // TODO Auto-generated method stub
   }

   @Override
   public void setDefaultInputs (long t0, long t1) {
      // TODO Auto-generated method stub
      
   }
   private int frameIntervalMsec = 0;
   public GLViewer myViewer;
   private class Animator extends Thread {
      int cnt = 0;
      int frame = first;
      public Animator () {
      }

      public synchronized void run () {
         try {
            Thread.sleep (1000);//1 second
         }
         catch (Exception e) {
         }
         long startTimeMsec = System.currentTimeMillis ();
         try
         {
            while (true) // do 1000 steps
            {
               if(Main.getScheduler ().isPlaying ())
               {
                  if(frame<=last)
                  {
                     PolygonalMesh tmesh = new PolygonalMesh();
                     String meshName = "C:/work/result/test11/processed_result_"+frame+".obj";
                     meshReader = new BufferedReader ( new FileReader (meshName));
                     tmesh.read(meshReader);
                     mesh.myVertices = tmesh.myVertices;
                     mesh.myNormalList = tmesh.myNormalList;
                     mesh.myNormalIndices = tmesh.myNormalIndices;
                     mesh.myNumVertices = tmesh.myNumVertices;
                     mesh.myFaces = tmesh.myFaces;
                     myViewer.rerender ();
                     meshReader.close ();
                     //tmesh.clear ();
                  }
                  frame += 10;
               }
               cnt++;
               long time = System.currentTimeMillis ();
               long waitTime = cnt * frameIntervalMsec + startTimeMsec - time;
               if (waitTime > 10) {
               try {
                  //Thread.sleep (frameIntervalMsec);
               }
               catch (Exception e) {
               }
               }
            }
         }
         catch (Exception e) {
         }
      }
   }

}


