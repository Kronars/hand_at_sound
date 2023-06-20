import supriya as sup
from time import sleep

s = sup.Server().boot()

from supriya.ugens import EnvGen, Out, SinOsc
from supriya.synthdefs import Envelope, synthdef

@synthdef()
def simple_sine(frequency=440, amplitude=0.1, gate=1):
    sine = SinOsc.ar(frequency=frequency) * amplitude
    envelope = EnvGen.kr(envelope=Envelope.adsr(), gate=gate, done_action=2)
    Out.ar(bus=0, source=[sine * envelope] * 2)

_ = s.add_synthdefs(simple_sine)


group = s.add_group()

for i in range(3):
    _ = group.add_synth(simple_sine, frequency=111 * (i + 1))

print(s.query_tree())

sleep(3)

for synth in group.children[:]:
    synth.free()

s.quit()
