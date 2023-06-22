s.reboot

// Запуск семпла
(
SynthDef("playWav",{ |out = 0, bufnum, rate=1|
    Out.ar(out,
        PlayBuf.ar(2, bufnum, rate)
    )
}).add;
)

// Фильтр
(
SynthDef(\lowPassFilter, {
    |in, freq = 2000, rq = 0.5, out|
    Out.ar(out, RLPF.ar(In.ar(in, 2), freq, rq) );
});
)

b = Buffer.read(s, Platform.resourceDir +/+ "sounds/Aquarius.wav");


~sampl = Synth(\playWav, [\bufnum, b]);
~filtr = Synth(\lowPassFilter, [\in, ~sampl, \freq, 2000, \rq, 0.5]);

synth.set(\freq, 1000);  // Установка среза частоты на 1000 Гц
synth.set(\rq, 0.3);    // Установка Q-фактора (резонанса) на 0.3

// Тесты
(
SynthDef(\lowPassFilter2, {
    |in, freq = 2000, rq = 0.5, out|
	Out.ar(out, RLPF.ar(In.ar(in, 2), freq, rq) );
}).play(s, args: [\in, ~sampl, \out, 1]);
)

{RLPF.ar(Saw.ar([100,250],0.1), 2000)}.play;
















































