package artisynth.models.necksim;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;

import javax.media.opengl.GL;

import maspack.geometry.PolygonalMesh;
import maspack.geometry.Vertex3d;
import maspack.matrix.Point3d;
import maspack.matrix.RigidTransform3d;
import maspack.properties.PropertyMode;
import maspack.render.GLRenderer;
import maspack.render.GLViewer;
import maspack.render.RenderProps;
import maspack.render.TextureProps;
import maspack.util.InternalErrorException;
import argparser.BooleanHolder;
import argparser.DoubleHolder;
import artisynth.core.driver.Main;
import artisynth.core.mechmodels.MechModel;
import artisynth.core.mechmodels.Particle;
import artisynth.core.modelbase.RenderableModelBase;
import artisynth.core.moviemaker.MovieMaker;
import artisynth.core.util.ArtisynthPath;

public class NeckSkin extends RenderableModelBase{
   
   public PolygonalMesh mesh = new PolygonalMesh();
   MechModel mechModel;
   ArrayList  pnames = new ArrayList();
   public NeckSkin() throws Exception
   {
      this(null, null);
   }
   public NeckSkin(String name) throws Exception
   {
      this(name, null);
   }
   public NeckSkin(String name, MechModel myMechModel) throws Exception
   {
      super(name);
      mechModel = myMechModel;
      String meshName = "src/artisynth/models/necksim/skin/headneckskin.obj";
      Reader meshReader = new BufferedReader ( new FileReader (
                                                  ArtisynthPath.getHomeRelativeFile (meshName, ".")));
      mesh.read(meshReader);
      mesh.setFixed(false);
      mesh.scale (GlobalParameters.scale_to_human);
      SkinSpringMesh.createSpringMesh (mesh, myMechModel);
      pnames = SkinSpringMesh.pnames;
      myRenderProps = RenderProps.createMeshProps(null);
      myRenderProps.setFaceColor(new Color(1f, 0.8f, 0.6f));
      myRenderProps.setAlpha (0);
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
   private int frameIntervalMsec = 50;
   public GLViewer myViewer;
   FileWriter fw;
   BufferedWriter bw;
   private class Animator extends Thread {
      int cnt = 0;
      int frameid = 0; //pitch 0-359 roll: 358-621 yawtoback : 620-735 yawtofront 734-851
      int interval = 50;
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
                  for(int i=0; i< pnames.size (); i++)
                  {
                     String name =(String) pnames.get (i);
                     int id = Integer.parseInt (name.split ("_")[2]);
                     Particle p = (Particle)mechModel.particles ().get (name);
                     Vertex3d vtx = (Vertex3d)mesh.myVertices.get (id);
                     vtx.pnt.set(p.getPosition ());
                  }
                  /*fw = new FileWriter(ArtisynthPath.getHomeRelativeFile ("src/artisynth/models/necksim/result/deform_"+frameid+".txt", "."));
                  bw = new BufferedWriter(fw);
                  for(int i = 0; i< mesh.myVertices.size (); i++)
                  {
                     Vertex3d vtx = (Vertex3d)mesh.myVertices.get (i);
                     bw.write (frameid+" "+vtx.pnt.x+" "+vtx.pnt.y+" "+vtx.pnt.z);
                     bw.newLine ();
                  }
                  bw.flush ();
                  bw.close ();
                  fw.close ();*/
                  if(frameid%interval==0)
                  {
                  PrintStream ps =
                  new PrintStream (
                     new FileOutputStream (ArtisynthPath.getHomeRelativeFile ("src/artisynth/models/necksim/result/result_"+frameid/interval+".obj", "."))
                  );
                  mesh.write (ps, "%g");
                  }
                  try {
                     //MovieMaker maker = new MovieMaker (myViewer);
                    // maker.grabScreenShot("screenshot"+cnt+".png");
                  }
                  catch (Exception e1) {
                     throw new InternalErrorException ("Cannot create movie maker");
                  }
                  frameid++;
               }
               myViewer.rerender ();
               cnt++;
               long time = System.currentTimeMillis ();
               long waitTime = cnt * frameIntervalMsec + startTimeMsec - time;
               if (waitTime > 10) {
               try {
                  Thread.sleep (frameIntervalMsec);
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

