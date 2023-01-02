class Source(Component):
    def __init__(self):
        super(Source, self).__init__()
        self.name = "Source"
        self.audioClip = None
        self.pitch = 1
        self.gain = 1
        self.loop = False

    def play(self):
        CALL_COMPONENT_METHOD(self, "Play", [])

    def pause(self):
        CALL_COMPONENT_METHOD(self, "Pause", [])

    def stop(self):
        CALL_COMPONENT_METHOD(self, "StopPlay", [])

    def setPitch(self, pitch):
        self.pitch = pitch
        SET_COMPONENT_ATTRIBUTE(self, "pitch", pitch)

    def setGain(self, gain):
        self.gain = gain
        SET_COMPONENT_ATTRIBUTE(self, "gain", gain)

    def setLoop(self, loop):
        self.loop = loop
        SET_COMPONENT_ATTRIBUTE(self, "loop", loop)

    def setAudioClip(self, audioClip):
        self.audioClip = audioClip
        CALL_COMPONENT_METHOD(self, "SetAudioClip", [audioClip.path])