// --------------------- Эффекты -----------------------

// Вспомогательное - контроль количества эффектов
~fxRatio = { |out, sigBefore, sigAfter, ratio|
	Out.ar(out, 0.25 * sigBefore * (1 - ratio));
	Out.ar(out, 0.25 * sigAfter * ratio);
	// помножение на 0.25 - костыль для компенсации сложения громкостей эффектов
	// не ебу как зароутить без воспроизведения на каждом эффекте
};

// Нерабочее воспроизведение трека по пути
// SynthDef(\playWav, { |fxBus, path, rate=1|
// 	var sig, buf;
// 	buf = Buffer.read(s, path);
// 	sig = PlayBuf.ar(2, buf, doneAction: Done.freeSelf);
// 	// Out.ar(fxBus, sig);
// 	Out.ar(0, sig);
// }).add;


// Чтение со внешнего источника
SynthDef(\readSound, { |fxBus, input|
	Out.ar(fxBus, SoundIn.ar(input));
}).add;

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

Synth.head(nil, \readSound, [\fxBus, ~bus, \input, 1]);

// Server.killAll

// Посмотреть аудио входы
/*ServerOptions.inDevices.do { |elem|
	elem.postln;
};*/

// ~fltr.set(\ratio, 1, \freq, 2000);
// ~dist.set(\ratio, 0, \bias, 0.5, \gain, 0.2);
// ~revb.set(\ratio, 1, \room, 0.7);
// ~dely.set(\ratio, 1, \delay, 0.25);

// ------------------ OSC обработчики -------------------

~all_fx = { |ratio|
	~fltr.set(\ratio, ratio);
	~dist.set(\ratio, ratio);
	~dely.set(\ratio, ratio);
	~revb.set(\ratio, ratio);
};

// ~all_fx.value(0);

OSCFunc({|msg, time, addr, recvPort|
	~all_fx.value(msg[1]);
}, "/fx/all/dry-wet" );


OSCFunc({|msg, time, addr, recvPort|
	~fltr.set(\freq, msg[1]);
}, "/fx/filter/freq" );

OSCFunc({|msg, time, addr, recvPort|
	~fltr.set(\ratio, msg[1]);
}, "/fx/filter/dry-wet" );


OSCFunc({|msg, time, addr, recvPort|
	~dist.set(\bias, msg[1]);
}, "/fx/distort/bias" );

OSCFunc({|msg, time, addr, recvPort|
	~dist.set(\ratio, msg[1]);
}, "/fx/distort/dry-wet" );


OSCFunc({|msg, time, addr, recvPort|
	~dely.set(\ratio, msg[1]);
}, "/fx/delay/dry-wet" );

OSCFunc({|msg, time, addr, recvPort|
	~revb.set(\ratio, msg[1]);
}, "/fx/reverb/dry-wet" );

// Тест
// m = NetAddr("127.0.0.1", NetAddr.langPort); // loopback
// m.sendMsg("/fx/all/dry-wet", 0.1);
