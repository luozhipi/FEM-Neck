package artisynth.models.necksim;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;

import maspack.geometry.OBBTree;
import maspack.geometry.PolygonalMesh;
import maspack.matrix.Point3d;
import maspack.matrix.Vector3d;
import maspack.render.GLRenderable;
import maspack.render.GLRenderer;
import maspack.render.GLViewer;
import maspack.render.GLViewerFrame;
import maspack.render.RenderList;
import maspack.render.RenderProps;
import maspack.render.RenderProps.PointStyle;
import maspack.render.RenderProps.Shading;
import argparser.IntHolder;
import artisynth.core.femmodels.FemModel3d;
import artisynth.core.femmodels.FemModel.SurfaceRender;
import artisynth.core.util.ArtisynthPath;

public class TestOBB extends GLViewerFrame implements ActionListener, GLRenderable {
   private static final long serialVersionUID = 1L;
   static String  path = "src/artisynth/models/necksim/skin/";
   public TestOBB (int width, int height) throws Exception {
      super ("testing", width, height);
      PolygonalMesh mesh2 = new PolygonalMesh();
      String name = "skeleton";
      Reader meshReader;
      meshReader = new BufferedReader ( new FileReader (
         ArtisynthPath.getHomeRelativeFile (path+name+".obj", ".")));
      mesh2.read(meshReader);
      mesh2.scale (GlobalParameters.scale_to_human);
      OBBTree obbt = mesh2.getObbtree();
      viewer.addRenderable (obbt);
      viewer.autoFit ();
      viewer.setBackgroundColor (0.8f, 0.8f, 0.8f);
   }
   @Override
   public void prerender (RenderList list) {
      // TODO Auto-generated method stub
      
   }
   @Override
   public void render (GLRenderer renderer) {
      // TODO Auto-generated method stub
      
   }
   @Override
   public void updateBounds (Point3d pmin, Point3d pmax) {
      // TODO Auto-generated method stub
      
   }
   @Override
   public int getRenderHints () {
      // TODO Auto-generated method stub
      return 0;
   }
   @Override
   public void actionPerformed (ActionEvent arg0) {
      // TODO Auto-generated method stub
      
   }
   public static void main (String[] args) throws Exception
   {
      IntHolder width = new IntHolder (1000);
      IntHolder height = new IntHolder (800);
      TestOBB frame =
      new TestOBB (width.value, height.value);
      frame.setVisible (true);
   }
}
