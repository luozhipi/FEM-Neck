package artisynth.models.necksim;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import maspack.geometry.PolygonalMesh;
import maspack.render.RenderProps;
import artisynth.core.mechmodels.MechModel;
import artisynth.core.mechmodels.RigidBody;
import artisynth.core.util.ArtisynthPath;

public class LoadMuscleGeo {
   
   public static void load(MechModel myMechModel)
   {
      String meshFilename = ArtisynthPath.getSrcRelativePath(NeckSkeleton.class,
         "rigid/mba023.obj");
      PolygonalMesh mesh = new PolygonalMesh();
      try {
         mesh.read(new BufferedReader(new FileReader(meshFilename)));
      } catch (IOException e) {
         e.printStackTrace();
         return;
      }
      mesh.scale (GlobalParameters.scale_to_human);
      RigidBody body;
      body = new RigidBody();
      body.setName ("f");
      body.setMesh(mesh, meshFilename);
      myMechModel.addRigidBody(body);
      RenderProps.setShading(body, RenderProps.Shading.GOURARD);
      RenderProps.setFaceColor(body, Color.red);
      RenderProps.setDrawEdges (body, true);
      RenderProps.setShininess (body, 1);
      RenderProps.setLineColor (body, Color.blue);
      RenderProps.setFaceStyle(body, RenderProps.Faces.FRONT_AND_BACK);
   }

}
