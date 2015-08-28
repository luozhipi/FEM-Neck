Anatomically physical modeling and simulation of the Human Neck.

Based on [ArtiSynth], a biomechanical human neck model is developed in JAVA language, for modeling and simulating the physical dynamics 
and bio-mechinics of the human neck.


After downloading the ArtiSynth, then copy the entire fold `necksim` to `src/artisynth/models/`. Then you can use the model, and modify as you want.

Features:

1: Finite Element Modeling/Simulation with  co-rotational material

2: mass-spring network representing the face

3: soft constraints for coupling hard-soft bodies

4: anatomically and bio-mechanically based

5: linearized model, trading speed againt accuracy

6: port to [FEBio] for validation

7: meshing based on [TetGen]



[ArtiSynth]: http://artisynth.magic.ubc.ca/artisynth/
[FEBio]: http://febio.org/febio/
[TetGen]: http://wias-berlin.de/software/tetgen/
