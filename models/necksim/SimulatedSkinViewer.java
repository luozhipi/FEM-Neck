package artisynth.models.necksim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import javax.media.opengl.GL;
import maspack.render.*;
import maspack.render.RenderProps.Shading;
import maspack.render.RenderProps.Faces;
import maspack.util.InternalErrorException;
import maspack.matrix.*;
import maspack.geometry.PolygonalMesh;
import argparser.IntHolder;
import artisynth.core.femmodels.FemModel3d;
import artisynth.core.femmodels.TetGenReader;
import artisynth.core.moviemaker.MovieMaker;
import artisynth.core.util.ArtisynthPath;

public class SimulatedSkinViewer extends GLViewerFrame
   implements ActionListener, GLRenderable {
   private static final long serialVersionUID = 1L;
   RenderProps myRenderProps;
   private RenderProps createRenderProps (PolygonalMesh mesh) {
      RenderProps props = mesh.createRenderProps();
      props.setShading (Shading.GOURARD);
      props.setFaceStyle (Faces.FRONT_AND_BACK);
      props.setDrawEdges (false);
      props.setLineColor(Color.gray);
      props.setFaceColor (new Color(1.0f, 0.8f, 0.6f));
      props.setAlpha (1);
      return props;
   }

   public SimulatedSkinViewer (PolygonalMesh skin,int w, int h) {
      super ("SimulatedSkinViewer", w, h);

      viewer.addRenderable (skin);
      myRenderProps = createRenderProps (skin);
      skin.setRenderProps (myRenderProps);
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
      PolygonalMesh skin = new PolygonalMesh();
      String geometry = "src/artisynth/models/necksim/result/";
      String name = "processed_result_116.obj";
      try {
         Reader meshReader = new BufferedReader ( new FileReader (
            ArtisynthPath.getHomeRelativeFile (geometry+name, ".")));
         skin.read(meshReader);
         
      }
      catch (Exception e) {
         e.printStackTrace();
         System.exit(1);
      }
      SimulatedSkinViewer frame =
         new SimulatedSkinViewer (skin,width.value, height.value);
      frame.setVisible (true);
   }
}
