# Import the package and create an audio effects chain function.
from pysndfx import AudioEffectsChain

fx = (
    AudioEffectsChain()
    .highshelf()
    .reverb()
    .phaser()
    .delay()
    .lowshelf()
)

infile = 'Aquarius.wav'
outfile = 'Aquarius_effected.wav'

print("до эффектов")
# Apply phaser and reverb directly to an audio file.
# fx(infile, outfile)
