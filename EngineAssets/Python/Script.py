class Script:
    def __init__(self, gid, name):
        self.gid = gid
        self.name = name

    def call(self, method):
        return CALL_SCRIPT_METHOD(self.gid, self.name, method)

    def getVar(self, var):
        return GET_SCRIPT_VAR(self.gid, self.name, var)