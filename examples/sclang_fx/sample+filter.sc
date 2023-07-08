s.quit
s.boot
s.freeAll
Server.killAll

(
// --------------------- Эффекты -----------------------
// Проигрывание трека в два канала c эффектами
~fxRatio = { |out, sigBefore, sigAfter, ratio|
	Out.ar(out, 0.25 * sigBefore * (1 - ratio));         // 0.25 - костыль что бы нормализовать уровень громкости
	Out.ar(out, 0.25 * sigAfter * ratio);
};

SynthDef(\playWav, { |fxBus, bufnum, rate=1, ratio=1|
	var sig;
	sig = PlayBuf.ar(2, bufnum, rate);
	// Out.ar(fxBus, sig * (1 - ratio));
	Out.ar(fxBus,  sig);
}).add;

// Лоу пасс фильтр
SynthDef(\lowPassFilter, {
    |fxBus, out=0, freq = 2000, rq = 0.5, ratio = 0|
	var sig;
	sig = In.ar(fxBus, 2);
	sig = RLPF.ar(sig, MouseX.kr(-10, 6000), 0.3);
	~fxRatio.value(out, In.ar(fxBus, 2), sig, ratio);
}).add;

// Делей
SynthDef(\delay, { |fxBus, out=0, delay = 0.25, ratio = 0|
	var sig;
	sig = In.ar(fxBus, 2);
	sig = DelayL.ar(sig, delay, delay);
	~fxRatio.value(fxBus, In.ar(fxBus, 2), sig, ratio);
}).add;

// Реверб
SynthDef(\reverb, { |fxBus, out=0, ratio=0, room=0.5|
	var sig;
	sig = In.ar(fxBus, 2);
	sig = FreeVerb.ar(sig, 1, room);
	~fxRatio.value(fxBus, In.ar(fxBus, 2), sig, ratio);
	// Out.ar(fxBus, sig);
}).add;

// Дисторшн
SynthDef(\dist, { |fxBus, out=0, gain=0.0, bias=1, ratio=0|
	var sig;
	sig = In.ar(fxBus, 2);
	sig = AnalogVintageDistortion.ar(sig, gain, bias);
	~fxRatio.value(fxBus, In.ar(fxBus, 2), sig, ratio);
}).add;
)


(
// ------------------ Инициализация ---------------------
// Шина аудиопотока
~bus = Bus.audio(s, 2);
// Буффер с треком
~buf = Buffer.read(s, Platform.resourceDir +/+ "sounds/Aquarius.wav");


// -------------------- Запуск --------------------------
// Запуск с эффектами в 0
~fltr = Synth(\lowPassFilter,        [\fxBus, ~bus]);
~dist = Synth.before(~fltr, \dist,   [\fxBus, ~bus]);
~dely = Synth.before(~fltr, \delay,  [\fxBus, ~bus]);
~revb = Synth.before(~fltr, \reverb, [\fxBus, ~bus]);

Synth.head(nil, \playWav, [\fxBus, ~bus, \bufnum, ~buf]);
)

~fltr.set(\ratio, 0);
~dist.set(\ratio, 0, \bias, 0.5, \gain, 0.2);
~revb.set(\ratio, 1, \room, 0.7);
~dely.set(\ratio, 0.4, \delay, 0.23);

s.plotTree