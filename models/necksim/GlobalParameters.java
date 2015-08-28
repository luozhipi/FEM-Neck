package artisynth.models.necksim;

import maspack.matrix.Point3d;

public class GlobalParameters {
  
   public static boolean showMuscle = true;
   public static boolean showBone = true;
   public static boolean showMS = false;
   public static boolean showCS = false;
   public static boolean showds = false;
   public static boolean showhill = true; //Hill muscles
   public static boolean showas = true; //adam's apple springs
   
   public static double scale_to_human = 0.01;
   
   public static Point3d basePivot = new Point3d(-0.0002*scale_to_human,1514.9977*scale_to_human,16.6207*scale_to_human);
   //public static Point3d skullPivot = new Point3d(0.1739*scale_to_human, 1829.1700*scale_to_human, 16.3910*scale_to_human);
   public static Point3d skullPivot = new Point3d(0.1739*scale_to_human, 1853.6272*scale_to_human, 16.3910*scale_to_human);
   public static Point3d jawPivot = new Point3d(-0.0003*scale_to_human, 1762.1652*scale_to_human, 70.3828*scale_to_human);
   public  static Point3d c1Pivot = new Point3d(-0.0002*scale_to_human, 1780.8715*scale_to_human, -3.0744*scale_to_human);
   public  static Point3d c2Pivot = new Point3d(-0.0002*scale_to_human, 1763.5255*scale_to_human, -5.6807*scale_to_human);
   public static Point3d c3Pivot = new Point3d(-0.0002*scale_to_human, 1735.8210*scale_to_human, -1.7793*scale_to_human);
   public static Point3d c4Pivot = new Point3d(0.1217*scale_to_human, 1718.3447*scale_to_human, -1.0366*scale_to_human);
   public static Point3d c5Pivot = new Point3d(-0.1413*scale_to_human, 1698.6720*scale_to_human, -3.7067*scale_to_human);
   public  static Point3d c6Pivot = new Point3d(-0.1340*scale_to_human, 1678.8831*scale_to_human, -8.3798*scale_to_human);
   public static Point3d c7Pivot = new Point3d(-0.0904*scale_to_human, 1657.7552*scale_to_human, -16.3776*scale_to_human);
   public static Point3d cricoidPivot = new Point3d(-0.0672*scale_to_human, 1676.3303*scale_to_human, 45.6986*scale_to_human);
   public static Point3d thyroidPivot = new Point3d(-0.0119*scale_to_human, 1694.5593*scale_to_human, 52.8611*scale_to_human);
   public static Point3d hyoidPivot = new Point3d(-0.2363*scale_to_human, 1730.8012*scale_to_human, 50.8564*scale_to_human);
   
   public static Point3d c7_base_joint = new Point3d(1.0500*scale_to_human, 1640.7697*scale_to_human, 9.2477*scale_to_human);
   public static Point3d c6_c7_joint = new Point3d(0.3242*scale_to_human, 1653.3906*scale_to_human, 10.1388*scale_to_human);
   public static Point3d c5_c6_joint = new Point3d(-0.4697*scale_to_human, 1676.1289*scale_to_human, 13.0044*scale_to_human);
   public static Point3d c4_c5_joint = new Point3d(-0.8596*scale_to_human, 1698.7175*scale_to_human, 14.1713*scale_to_human);
   public static Point3d c3_c4_joint = new Point3d(-0.2659*scale_to_human, 1723.3190*scale_to_human, 14.2531*scale_to_human);
   public static Point3d c2_c3_joint = new Point3d(-0.5033*scale_to_human, 1743.0095*scale_to_human, 16.4327*scale_to_human);
   public static Point3d c1_c2_joint = new Point3d(-0.0778*scale_to_human, 1763.9601*scale_to_human, 9.4363*scale_to_human);
   public static Point3d skull_c1_joint = new Point3d(-0.5013*scale_to_human, 1801.3612*scale_to_human, 1.9984*scale_to_human);
   public static Point3d skull_jaw_joint = new Point3d(0*scale_to_human, 1795.585*scale_to_human, 48.579*scale_to_human);
   
   public static Point3d cricothryroidArticulation = new Point3d(-0.043*scale_to_human, 1675.929*scale_to_human, 36.191*scale_to_human);
   
   public static  double thMemStiff = 2000.0; //kg/cm^2 // 4000 for jaw open
   public static   double thMemDamp = 0.10;

   public static  double ctrMemStiff = 4000.0; //6000
   public static  double ctrMemDamp = 0.10;
   
   public static  double defaultPharnyxStiffness = 2000;//2000 for jaw open
   public static  double pharnyxDamp = 0.0;
   
   public static double epi_sk = 20000;//12000
   public static double epi_sd = 1;//1000;
   public static double epi_pd = 0.4;//0.4;
   public static double epi_pmass = 0.01;//1;
   
   public static double inner_sk = 5000; //5000
   public static double inner_sd = 1;
   
   public static double deep_sk = 20000;
   public static double deep_sd = 1;
   
   public static double mySkullDampingT = 30490;//for jaw open 30490
   public static double mySkullDampingR = 6000;
   
   public static double myJawDampingT = 30490;
   public static double myJawDampingR = 6000;
   
   public static double myHyoidDampingT  = 15000;
   public static double myHyoidDampingR = 1000;
   
   public static double myThyroidDampingT = 150.00;
   public static double myThyroidDampingR  =10.00;
   
   public static double myCricoidDampingT = 150.00;
   public static double myCricoidDampingR = 10.00;
   
   public static double myC1DampingT = 15000;
   public static double myC1DampingR = 1000;
   
   public static double myC2DampingT = 15000;
   public static double myC2DampingR = 1000;
   
   public static double myC3DampingT = 15000;
   public static double myC3DampingR = 1000;
   
   public static double myC4DampingT = 15000;
   public static double myC4DampingR = 1000;
   
   public static double myC5DampingT = 1500;
   public static double myC5DampingR = 1000;
   
   public static double myC6DampingT = 15000;
   public static double myC6DampingR = 1000;
   
   public static double myC7DampingT = 15000;
   public static double myC7DampingR = 1000;
   
   public static double skullMass = 4.502+0.514;
   public static double jawMass = 0.514;
   public static double hyoidMass = 0.097;
   public static double thyroidMass = 0.168; 
   public static double cricoidMass = 0.168;
   public static double c1Mass = 0.160;
   public static double c2Mass = 0.257;
   public static double c3Mass = 0.222;
   public static double c4Mass = 0.230;
   public static double c5Mass = 0.301;
   public static double c6Mass = 0.186;
   public static double c7Mass = 0.284;
}
