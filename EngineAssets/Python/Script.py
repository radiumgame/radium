class Script:
    def __init__(self, gid, name):
        self.gid = gid
        self.name = name

    def call(self, method):
        CALL_SCRIPT_METHOD(self.gid, self.name, method)