NetAddr.localAddr
s.boot


SynthDef(\test, { |freq=440|
	Out.ar(0, SinOsc.ar(freq));
}).add;

~test = Synth(\test, [\freq, 440]);

o = OSCFunc.new(
	{
		|msg, time, addr, recvPort|
		~test.set(\freq, msg);
	},
	'/track/select'
);

o.free

m = NetAddr("127.0.0.1", NetAddr.langPort); // loopback
m.sendMsg("/track/select", 1100);