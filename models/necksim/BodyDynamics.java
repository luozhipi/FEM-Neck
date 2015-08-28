package artisynth.models.necksim;

import java.awt.Color;
import maspack.render.RenderProps;
import maspack.render.RenderProps.PointStyle;
import artisynth.core.mechmodels.FrameMarker;
import artisynth.core.mechmodels.MechModel;
import artisynth.core.mechmodels.RigidBody;

public class BodyDynamics{
  
   protected static MechModel mechModel;
   
   public static void setDampingProp(MechModel myMechModel)
   {
      mechModel = myMechModel;
      for(RigidBody body : mechModel.rigidBodies ())
      {
         setBodyDynamicProps(body.getName ());
      }
      setBodyDamping("skull", GlobalParameters.mySkullDampingT, GlobalParameters.mySkullDampingR);
      setBodyDamping("jaw", GlobalParameters.myJawDampingT, GlobalParameters.myJawDampingR);
      setBodyDamping("hyoid", GlobalParameters.myHyoidDampingT , GlobalParameters.myHyoidDampingR);
      setBodyDamping("thyroid", GlobalParameters.myThyroidDampingT, GlobalParameters.myThyroidDampingR );
      setBodyDamping("cricoid", GlobalParameters.myCricoidDampingT, GlobalParameters.myCricoidDampingR);
      setBodyDamping("C1", GlobalParameters.myC1DampingT, GlobalParameters.myC1DampingR);
      setBodyDamping("C2", GlobalParameters.myC2DampingT, GlobalParameters.myC2DampingR);
      setBodyDamping("C3", GlobalParameters.myC3DampingT, GlobalParameters.myC3DampingR);
      setBodyDamping("C4", GlobalParameters.myC4DampingT, GlobalParameters.myC4DampingR);
      setBodyDamping("C5", GlobalParameters.myC5DampingT, GlobalParameters.myC5DampingR);
      setBodyDamping("C6", GlobalParameters.myC6DampingT, GlobalParameters.myC6DampingR);
      setBodyDamping("C7", GlobalParameters.myC7DampingT, GlobalParameters.myC7DampingR);
   }
   public static void setBodyDamping(String name, double td, double rd)
   {
      RigidBody body = mechModel.getMyRigidBodies().get(name);
      if (body == null) { return; }
      body.setFrameDamping(td);
      body.setRotaryDamping(rd);
   }
   public static void setBodyDynamicProps(String name) {
      RigidBody body = mechModel.getMyRigidBodies().get(name);
      if (body == null) return;
      if (name.compareTo("skull") == 0.0) setSkullDynamicProps(body);
      else if (name.compareTo("jaw") == 0.0) setJawDynamicProps(body);
      else if (name.compareTo("hyoid") == 0.0) setHyoidDynamicProps(body);
      else if (name.compareTo("thyroid") == 0.0) setThyroidDynamicProps(body);
      else if (name.compareTo("cricoid") == 0.0) setCricoidDynamicProps(body);
      else if (name.compareTo("C1") == 0.0) setC1DynamicProps(body);
      else if (name.compareTo("C2") == 0.0) setC2DynamicProps(body);
      else if (name.compareTo("C3") == 0.0) setC3DynamicProps(body);
      else if (name.compareTo("C4") == 0.0) setC4DynamicProps(body);
      else if (name.compareTo("C5") == 0.0) setC5DynamicProps(body);
      else if (name.compareTo("C6") == 0.0) setC6DynamicProps(body);
      else if (name.compareTo("C7") == 0.0) setC7DynamicProps(body);
      else
      {
         setBodyFixed(body);
      }
   }
   public static void setBodyFixed(RigidBody body) {
      if (body != null) body.setDynamic(false);
   }
   
   public static void renderCOM(FrameMarker marker)
   {
      double comRadius =0.5*GlobalParameters.scale_to_human;
      RenderProps fixedProps = marker.createRenderProps();
      fixedProps.setPointColor(Color.red);
      fixedProps.setPointStyle (PointStyle.SPHERE);
      fixedProps.setPointRadius(comRadius);
      fixedProps.setVisible (false);
      marker.setRenderProps(fixedProps);
   }
   
   public static void setJawDynamicProps(RigidBody jaw)
   {
      if (jaw == null) { return; }
      jaw.setDynamic(true);
      jaw.setMass(GlobalParameters.jawMass);
      jaw.setInertiaFromMass (GlobalParameters.jawMass);
      FrameMarker marker = new FrameMarker();
      marker.setName("jawcom");
      renderCOM(marker);
      mechModel.addFrameMarker(marker, jaw, jaw.getCenterOfMass ());
   }
   
   public static void setSkullDynamicProps(RigidBody skull)
   {
      if (skull == null) { return; }
      skull.setDynamic(true);
      skull.setMass(GlobalParameters.skullMass);
      skull.setInertiaFromMass (GlobalParameters.skullMass);
      FrameMarker marker = new FrameMarker();
      marker.setName("skullcom");
      renderCOM(marker);
      mechModel.addFrameMarker(marker, skull, skull.getCenterOfMass ());
   }
   public static void setC1DynamicProps(RigidBody C1) {
      if (C1 == null) { return; }
      C1.setDynamic(true);
      C1.setMass(GlobalParameters.c1Mass);
      C1.setInertiaFromMass (GlobalParameters.c1Mass);
      FrameMarker marker = new FrameMarker();
      marker.setName("C1com");
      renderCOM(marker);
      mechModel.addFrameMarker(marker, C1, C1.getCenterOfMass ());
   }
   public static void setC2DynamicProps(RigidBody C2) {
      if (C2 == null) { return; }
      C2.setDynamic(true);
      C2.setMass(GlobalParameters.c2Mass);
      C2.setInertiaFromMass (GlobalParameters.c2Mass);
      FrameMarker marker = new FrameMarker();
      marker.setName("C2com");
      renderCOM(marker);
      mechModel.addFrameMarker(marker, C2, C2.getCenterOfMass ());
   }
   public static void setC3DynamicProps(RigidBody C3) {
      if (C3 == null) { return; }
      C3.setDynamic(true);
      C3.setMass(GlobalParameters.c3Mass);
      C3.setInertiaFromMass (GlobalParameters.c3Mass);
      FrameMarker marker = new FrameMarker();
      marker.setName("C3com");
      renderCOM(marker);
      mechModel.addFrameMarker(marker, C3, C3.getCenterOfMass ());    
   }
   public static void setC4DynamicProps(RigidBody C4) {
      if (C4 == null) { return; }
      C4.setDynamic(true);
      C4.setMass(GlobalParameters.c4Mass);
      C4.setInertiaFromMass (GlobalParameters.c4Mass);
      FrameMarker marker = new FrameMarker();
      marker.setName("C4com");
      renderCOM(marker);
      mechModel.addFrameMarker(marker, C4, C4.getCenterOfMass ());
   }
   public static void setC5DynamicProps(RigidBody C5) {
      if (C5 == null) { return; }
      C5.setDynamic(true);
      C5.setMass(GlobalParameters.c5Mass);
      C5.setInertiaFromMass (GlobalParameters.c5Mass);
      FrameMarker marker = new FrameMarker();
      marker.setName("C5com");
      renderCOM(marker);
      mechModel.addFrameMarker(marker, C5, C5.getCenterOfMass ());
   }
   public static void setC6DynamicProps(RigidBody C6) {
      if (C6 == null) { return; }
      C6.setDynamic(true);
      C6.setMass(GlobalParameters.c6Mass); 
      C6.setInertiaFromMass (GlobalParameters.c6Mass);
      FrameMarker marker = new FrameMarker();
      marker.setName("C6com");
      renderCOM(marker);
      mechModel.addFrameMarker(marker, C6, C6.getCenterOfMass ());
   }
   public static void setC7DynamicProps(RigidBody C7) {
      if (C7 == null) { return; }
      C7.setDynamic(true);
      C7.setMass(GlobalParameters.c7Mass); 
      C7.setInertiaFromMass (GlobalParameters.c7Mass);
      FrameMarker marker = new FrameMarker();
      marker.setName("C7com");
      renderCOM(marker);
      mechModel.addFrameMarker(marker, C7, C7.getCenterOfMass ());     
   }
   public static void setThyroidDynamicProps(RigidBody thyroid) {
      if (thyroid == null) { return; }
      thyroid.setDynamic(true);
      thyroid.setMass(GlobalParameters.thyroidMass);
      thyroid.setInertiaFromMass (GlobalParameters.thyroidMass);
      FrameMarker marker = new FrameMarker();
      marker.setName("thyroidcom");
      renderCOM(marker);
      mechModel.addFrameMarker(marker, thyroid, thyroid.getCenterOfMass ());
   }

   public static void setCricoidDynamicProps(RigidBody cricoid) {
      if (cricoid == null) { return; }
      cricoid.setDynamic(true);
      cricoid.setMass(GlobalParameters.cricoidMass);
      cricoid.setInertiaFromMass (GlobalParameters.cricoidMass);
      FrameMarker marker = new FrameMarker();
      marker.setName("cricoidcom");
      renderCOM(marker);
      mechModel.addFrameMarker(marker, cricoid, cricoid.getCenterOfMass ());
   }
   public static void setHyoidDynamicProps(RigidBody hyoid) {
      if (hyoid == null) { return; }
      hyoid.setDynamic(true);
      hyoid.setMass(GlobalParameters.hyoidMass);
      hyoid.setInertiaFromMass (GlobalParameters.hyoidMass);
      FrameMarker marker = new FrameMarker();
      marker.setName("hyoidcom");
      renderCOM(marker);
      mechModel.addFrameMarker(marker, hyoid, hyoid.getCenterOfMass ());
   }

}
