package artisynth.models.necksim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.*;
import java.awt.Color;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;

import javax.media.opengl.GL;

import maspack.render.*;
import maspack.render.RenderProps.PointStyle;
import maspack.render.RenderProps.Shading;
import maspack.render.RenderProps.Faces;
import maspack.util.InternalErrorException;
import maspack.matrix.*;
import maspack.collision.*;
import maspack.geometry.PolygonalMesh;

import maspack.properties.*;

import argparser.ArgParser;
import argparser.BooleanHolder;
import argparser.DoubleHolder;
import argparser.IntHolder;
import argparser.StringHolder;

import artisynth.core.femmodels.FemModel3d;
import artisynth.core.femmodels.TetGenReader;
import artisynth.core.femmodels.FemModel.SurfaceRender;
import artisynth.core.gui.*;
import artisynth.core.gui.widgets.ValueChangeEvent;
import artisynth.core.gui.widgets.ValueChangeListener;
import artisynth.core.moviemaker.MovieMaker;
import artisynth.core.util.ArtisynthPath;

public class MultiMeshViewer extends GLViewerFrame
   implements ActionListener, GLRenderable {
   private static final long serialVersionUID = 1L;

   RenderProps myRenderProps;
   
   PolygonalMesh myMesh023;
   PolygonalMesh myMesh015_a;
   PolygonalMesh myMesh016;
   PolygonalMesh myMesh020;
   
   FemModel3d mymba023;
   FemModel3d mymne015_a;
   FemModel3d mymne016;
   FemModel3d mymne020;

   Transrotator3d myDragger023;
   Transrotator3d myDragger015_a;
   Transrotator3d myDragger016;
   Transrotator3d myDragger020;


   private class DragHandler implements Dragger3dListener {
      PolygonalMesh myMesh;

      DragHandler (PolygonalMesh mesh) {
         myMesh = mesh;
      }
      public void draggerBegin (Dragger3dEvent e) {
      }

      public void draggerMove (Dragger3dEvent e) {
         RigidTransform3d Xinc = (RigidTransform3d)e.getIncrementalTransform();
         RigidTransform3d X = new RigidTransform3d();
         myMesh.getMeshToWorld (X);
         X.mul (Xinc);
         myMesh.setMeshToWorld (X);
      }

      public void draggerEnd (Dragger3dEvent e) {
      }
   }

   private RenderProps createRenderProps (PolygonalMesh mesh) {
      RenderProps props = mesh.createRenderProps();
      props.setShading (Shading.GOURARD);
      props.setFaceStyle (Faces.FRONT_AND_BACK);
      props.setDrawEdges (true);
      props.setLineColor(Color.gray);
      props.setFaceColor (Color.red);
      props.setAlpha (1);
      return props;
   }

   public MultiMeshViewer (PolygonalMesh mesh023, FemModel3d mba023, 
                           PolygonalMesh mesh015_a, FemModel3d mne015_a,
                           PolygonalMesh mesh016, FemModel3d mne016,
                           PolygonalMesh mesh020, FemModel3d mne020,
                           int w, int h) {
      super ("TwoMeshViewer", w, h);
      
      myMesh023  = mesh023;
      myMesh015_a  = mesh015_a;
      myMesh016  = mesh016;
      myMesh020  = mesh020;
      
      mymba023 = mba023;
      mymne015_a = mne015_a;
      mymne016 = mne016;
      mymne020 = mne020;

      viewer.addRenderable (myMesh023);
      viewer.addRenderable (myMesh015_a);
      viewer.addRenderable (myMesh016);
      viewer.addRenderable (myMesh020);
      
      viewer.addRenderable (mymba023);
      viewer.addRenderable (mymne015_a);
      viewer.addRenderable (mymne016);
      viewer.addRenderable (mymne020);
      
      mymba023.setSurfaceRendering (SurfaceRender.Shaded);
      mymne015_a.setSurfaceRendering (SurfaceRender.Shaded);
      mymne016.setSurfaceRendering (SurfaceRender.Shaded);
      mymne020.setSurfaceRendering (SurfaceRender.Shaded);
      
      double aphle = 1.0;
      boolean dedge =false;
      int lw = 2;
      RenderProps.setAlpha (mymba023, aphle);
      RenderProps.setAlpha (mymne015_a, aphle);
      RenderProps.setAlpha (mymne016, aphle);
      RenderProps.setAlpha (mymne020, aphle);
      
      RenderProps.setDrawEdges (mymba023, dedge);
      RenderProps.setDrawEdges (mymne015_a, dedge);
      RenderProps.setDrawEdges (mymne016, dedge);
      RenderProps.setDrawEdges (mymne020, dedge);
      
      RenderProps.setFaceColor (mymba023, Color.red);
      RenderProps.setFaceColor (mymne015_a, Color.red);
      RenderProps.setFaceColor (mymne016, Color.red);
      RenderProps.setFaceColor (mymne020, Color.red);
      
      RenderProps.setLineColor (mymba023, Color.blue);
      RenderProps.setLineColor (mymne015_a, Color.blue);
      RenderProps.setLineColor (mymne016, Color.blue);
      RenderProps.setLineColor (mymne020, Color.blue);
      
      RenderProps.setLineWidth (mymba023, lw);
      RenderProps.setLineWidth (mymne015_a, lw);
      RenderProps.setLineWidth (mymne016, lw);
      RenderProps.setLineWidth (mymne020, lw);
      
      myRenderProps = createRenderProps (myMesh023);
      myMesh023.setRenderProps (myRenderProps);
      myMesh015_a.setRenderProps (myRenderProps);
      myMesh016.setRenderProps (myRenderProps);
      myMesh020.setRenderProps (myRenderProps);

      viewer.autoFitPerspective (0);
      viewer.setBackgroundColor (0.8f, 0.8f, 0.8f);
      viewer.setAxisLength (0);

      viewer.addMouseInputListener (new MouseInputAdapter() {
         public void mousePressed (MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
               displayPopup (e);
            }
         }
      });

      viewer.addRenderable (this);
   }

   public void prerender (RenderList list) {
   }

   public void updateBounds (Point3d pmin, Point3d pmax) {
   }

   public int getRenderHints() {
      return 0;
   }

   public void render (GLRenderer renderer) {
      GL gl = renderer.getGL();
   }   

   public void actionPerformed (ActionEvent e) {
      String cmd = e.getActionCommand();
      System.out.println ("cmd="+ cmd);
      if (cmd.equals ("save image")) {
         try {
            MovieMaker maker = new MovieMaker (viewer);
            maker.grabScreenShot("screenshot.png");
         }
         catch (Exception e1) {
            throw new InternalErrorException ("Cannot create movie maker");
         }
      }
   }

   private JMenuItem createMenuItem (String cmd) {
      JMenuItem item = new JMenuItem (cmd);
      item.addActionListener (this);
      item.setActionCommand (cmd);
      return item;
   }

   private void displayPopup (MouseEvent evt) {
      JPopupMenu popup = new JPopupMenu();
      popup.add (createMenuItem ("save image"));
      popup.setLightWeightPopupEnabled (false);
      popup.show (evt.getComponent(), evt.getX(), evt.getY());
   }

   public static void main (String[] args) {
      IntHolder width = new IntHolder (640);
      IntHolder height = new IntHolder (480);
      
      String geometry = "src/artisynth/models/necksim/rigid/";
      String fem = ArtisynthPath.getHomeRelativePath("src/artisynth/models/necksim/fem/",".");
      
      PolygonalMesh mesh023 = new PolygonalMesh();
      FemModel3d mba023 = new FemModel3d ("mba023");   
      
      PolygonalMesh mesh015_a = new PolygonalMesh();
      FemModel3d mne015_a = new FemModel3d ("mne015_a");   

      PolygonalMesh mesh016 = new PolygonalMesh();
      FemModel3d mne016 = new FemModel3d ("mne016");   
      
      PolygonalMesh mesh020 = new PolygonalMesh();
      FemModel3d mne020 = new FemModel3d ("mne020"); 
      
      try {
         Reader meshReader = new BufferedReader ( new FileReader (
            ArtisynthPath.getHomeRelativeFile (geometry+"mba023.obj", ".")));
         mesh023.read(meshReader);
         mesh023.scale (GlobalParameters.scale_to_human);
         
         TetGenReader.read (
            mba023, 5000, fem + "r_mba023.1.node", fem +
            "r_mba023.1.ele", new Vector3d (GlobalParameters.scale_to_human,GlobalParameters.scale_to_human,GlobalParameters.scale_to_human));
         
         meshReader = new BufferedReader ( new FileReader (
            ArtisynthPath.getHomeRelativeFile (geometry+"mne015.obj", ".")));
         mesh015_a.read(meshReader);
         mesh015_a.scale (GlobalParameters.scale_to_human);
         
         MeshReader.read (
            mne015_a, 5000, fem + "r_mne015_a.1.node", fem +
            "r_mne015_a.1.ele", new Vector3d (GlobalParameters.scale_to_human,GlobalParameters.scale_to_human,GlobalParameters.scale_to_human));
         
         meshReader = new BufferedReader ( new FileReader (
            ArtisynthPath.getHomeRelativeFile (geometry+"mne016.obj", ".")));
         mesh016.read(meshReader);
         mesh016.scale (GlobalParameters.scale_to_human);
         
         MeshReader.read (
            mne016, 5000, fem + "r_mne016.1.node", fem +
            "r_mne016.1.ele", new Vector3d (GlobalParameters.scale_to_human,GlobalParameters.scale_to_human,GlobalParameters.scale_to_human));
         
         meshReader = new BufferedReader ( new FileReader (
            ArtisynthPath.getHomeRelativeFile (geometry+"mne020.obj", ".")));
         mesh020.read(meshReader);
         mesh020.scale (GlobalParameters.scale_to_human);
         
         MeshReader.read (
            mne020, 5000, fem + "r_mne020.1.node", fem +
            "r_mne020.1.ele", new Vector3d (GlobalParameters.scale_to_human,GlobalParameters.scale_to_human,GlobalParameters.scale_to_human));
         
      }
      catch (Exception e) {
         e.printStackTrace();
         System.exit(1);
      }

      MultiMeshViewer frame =
         new MultiMeshViewer (mesh023,mba023, mesh015_a, mne015_a, mesh016, mne016, mesh020, mne020, width.value, height.value);
      frame.setVisible (true);
   }
}
