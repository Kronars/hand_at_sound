NetAddr.localAddr
s.boot

(
o = OSCFunc.new(
	{
		|msg, time, addr, recvPort|
		[msg, time, addr, recvPort].postln;
	},
	'/track/select'
);
// o.trace(true);
)

o.free

m = NetAddr("127.0.0.1", NetAddr.langPort); // loopback
m.sendMsg("/track/select", "wtf");