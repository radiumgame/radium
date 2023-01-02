class AudioClip(object):
    def __init__(self, path):
        self.path = path
        audioData = RUN_COMMAND("LOAD_AUDIO", [path])
        self.length = audioData[0]
        self.formattedLength = audioData[1]