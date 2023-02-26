class Animation(Component):
    def __init__(self):
        super(Animation, self).__init__()
        self.name = "Animation"
        self.speed = 1
        self.loop = False
        self.playOnAwake = True

    def setSpeed(self, speed):
        self.speed = speed
        SET_COMPONENT_ATTRIBUTE(self, "animationSpeed", speed)

    def setLoop(self, loop):
        self.loop = loop
        SET_COMPONENT_ATTRIBUTE(self, "loop", loop)

    def setPlayOnAwake(self, poa):
        self.playOnAwake = poa
        SET_COMPONENT_ATTRIBUTE(self, "playOnAwake", poa)

    def play(self):
        CALL_COMPONENT_METHOD(self, "Play", [])

    def stop(self):
        CALL_COMPONENT_METHOD(self, "Stop", [])

    def pause(self):
        CALL_COMPONENT_METHOD(self, "Pause", [])

    def setAnimationClip(self, path):
        CALL_COMPONENT_METHOD(self, "SetAnimationClip", [path, True])