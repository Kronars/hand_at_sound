s.quit
s.boot
s.freeAll
currentEnvironment;

(
// --------------------- Эффекты -----------------------
// Проигрывание трека в два канала
SynthDef(\playWav, { |fxBus, bufnum, rate=1, dryWet = 0.5|
	var sig;
	sig = PlayBuf.ar(2, bufnum, rate);

	Out.ar(0, sig * dryWet);
	Out.ar(fxBus,  sig * (1 - dryWet));
}).add;

// Лоу пасс фильтр
SynthDef(\lowPassFilter, {
    |fxBus, out=0, freq = 2000, rq = 0.5|
	var sig;
	sig = In.ar(fxBus, 2);
	sig = RLPF.ar(sig, MouseX.kr(-10, 6000), 0.3);

	Out.ar(out, sig);
}).add;

// Делей
SynthDef(\delay, { |in, out = 0, delay = 0.25|
    Out.ar(
        out,
        DelayN.ar(
            In.ar(in, 2),
            delay,
            delay
        )
    )
}).add;

// Реверб
SynthDef(\reverb, { |in, out=0, dryWet, room=0.5|
	var sig;
	sig = In.ar(in, 2);
	sig = FreeVerb.ar(sig, MouseY.kr(1, 0), room);
	Out.ar(out, sig);
}).add;

// Дисторшн
SynthDef(\dist, { |fxBus, dryOut=0, gain=0.0, bias=0.8|
	var sig;
	sig = In.ar(fxBus, 2);
	sig = AnalogVintageDistortion.ar(sig, gain, bias);

	Out.ar(dryOut, sig);
}).add;
)



(
// ------------------ Инициализация ---------------------
// Шина аудиопотока
~bus = Bus.audio(s, 2);
// Буффер с треком
~buf = Buffer.read(s, Platform.resourceDir +/+ "sounds/Aquarius.wav");
// Группы воспроизведения
~src = Group.new;
~fx  = Group.after(~src);
)


(
// -------------------- Запуск --------------------------
// Запуск с эффектами в 0
// ~dely = Synth(\delay,  [\in, ~bus, \delay, 0], ~fx);
// ~revb = Synth(\reverb, [\in, ~bus, \room, 0], ~fx);
// ~dist = Synth(\dist,   [\fxBus, ~bus, \gain, 0], ~fx);
~fltr = Synth(\lowPassFilter, [\fxBus, ~bus], ~fx);

~smpl = Synth(\playWav, [\fxBus, ~bus, \bufnum, ~buf, \dryWet, 1], ~src);
)


// Влючение эффектов
~smpl.set(\dryWet, 0);

// Реверберации
~revb = Synth(\reverb, [\in, ~bus, \room, 0], ~fx);
~revb.set(\room, 1);

// Делей
~dely = Synth(\delay,  [\in, ~bus, \delay, 0], ~fx);
~dely.set(\delay, 1);

// Дисторшен
~dist = Synth(\dist,   [\fxBus, ~bus, \gain, 0], ~fx);
~dist.set(\gain, 0, \bias, 0.7);
~dist.set(\dryOut, 0);

// Выкл
~smpl.set(\dryWet, 1);
// Вкл
~smpl.set(\dryWet, 0);


s.freeAll