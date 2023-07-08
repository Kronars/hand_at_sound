// --------------------- Эффекты -----------------------

// Вспомогательное - контроль количества эффектов
~fxRatio = { |out, sigBefore, sigAfter, ratio|
	Out.ar(out, 0.25 * sigBefore * (1 - ratio));
	Out.ar(out, 0.25 * sigAfter * ratio);
	// помножение на 0.25 - костыль для компенсации сложения громкостей эффектов
	// не ебу как зароутить без воспроизведения на каждом эффекте
};

// Воспроизведение трека по пути
SynthDef(\playWav, { |fxBus, path, rate=1|
	var sig, buf;
	buf = Buffer.read(s, path);
	sig = PlayBuf.ar(2, buf, doneAction: Done.freeSelf);
	// Out.ar(fxBus, sig);
	Out.ar(0, sig);
}).add;

// Попытка запустить из буффера
// Synth.new(\playWav, [\fxBus, ~bus, \path, "D:/src/Projects/hand_at_sound/examples/soundCheck/Aquarius.wav"]);

// Лоу пасс фильтр
SynthDef(\lowPassFilter, {
    |fxBus, out=0, freq = 2000, rq = 0.5, ratio = 0|
	var sig;
	sig = In.ar(fxBus, 2);
	sig = RLPF.ar(sig, freq, 0.3);
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
}).add;

// Дисторшн
SynthDef(\dist, { |fxBus, out=0, gain=0.0, bias=1, ratio=0|
	var sig;
	sig = In.ar(fxBus, 2);
	sig = AnalogVintageDistortion.ar(sig, gain, bias);
	~fxRatio.value(fxBus, In.ar(fxBus, 2), sig, ratio);
}).add;


// ------------------ Инициализация ---------------------
// Шина аудиопотока
~bus = Bus.audio(s, 2);


// -------------------- Запуск --------------------------
// Запуск с эффектами в 0
~fltr = Synth(\lowPassFilter,        [\fxBus, ~bus]);
~dist = Synth.before(~fltr, \dist,   [\fxBus, ~bus]);
~dely = Synth.before(~dist, \delay,  [\fxBus, ~bus]);
~revb = Synth.before(~dely, \reverb, [\fxBus, ~bus]);

Synth.head(nil, \playWav, [\fxBus, ~bus, \bufnum, ~buf]);


// ~fltr.set(\ratio, 1);
// ~dist.set(\ratio, 0, \bias, 0.5, \gain, 0.2);
// ~revb.set(\ratio, 1, \room, 0.7);
// ~dely.set(\ratio, 0.4, \delay, 0.23);
