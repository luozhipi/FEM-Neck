package artisynth.models.necksim;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

public class TestLoadFEM extends GLViewerFrame implements ActionListener, GLRenderable {
   

   /**
    * 
    */
   private static final long serialVersionUID = 1L;
   public static String femPath =
   ArtisynthPath.getHomeRelativePath (
      "src/artisynth/models/necksim/fem/", ".");
   RenderProps myRenderProps;
   public TestLoadFEM (int width, int height) throws Exception {
      super ("testing", width, height);
      String name = "l_mne015_a";
      FemModel3d tissue = new FemModel3d (name);   
      MeshReader.read (
         tissue, 5000, femPath + name + ".1.node", femPath + name
         + ".1.ele", new Vector3d (1,1,1));
      myRenderProps = createRenderProps (tissue);
      tissue.setRenderProps (myRenderProps);
      viewer.addRenderable (tissue);
      viewer.autoFit ();
      viewer.setBackgroundColor (0.8f, 0.8f, 0.8f);
      System.out.println (tissue.getNodes ().size () +"_ele: "+tissue.getElements ().size ());
   }
   public static RenderProps createRenderProps(FemModel3d tissue)
   {
      RenderProps props = tissue.createRenderProps();
      tissue.setSurfaceRendering (SurfaceRender.Shaded);
      props.setDrawEdges (false);
      props.setFaceColor ( Color.red);
      props.setVisible (false);
      props.setShading ( Shading.GOURARD);
      props.setLineColor ( Color.blue);
      props.setLineWidth ( 5);
      props.setAlpha (0.5);
      props.setPointColor ( Color.white);
      props.setPointStyle (PointStyle.SPHERE);
      props.setPointRadius ( 0.1);
      return props;
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
      TestLoadFEM frame =
      new TestLoadFEM (width.value, height.value);
      frame.setVisible (true);
   }
}
