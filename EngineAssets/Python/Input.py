class Input:
    def __init__(self):
        raise Exception("Input class is not meant to be instantiated")

    @classmethod
    def isKeyDown(cls, key):
        return GET_KEYBOARD_INPUT("down", key)

    @classmethod
    def isMouseDown(cls, button):
        return GET_MOUSE_INPUT("down", button)

    @classmethod
    def isMouseReleased(cls, button):
        return GET_MOUSE_INPUT("up", button)

    @classmethod
    def isMousePressed(cls, button):
        return GET_MOUSE_INPUT("press", button)

    @classmethod
    def getMousePosition(cls):
        return Vector2(RUN_COMMAND("MOUSE_X", []), RUN_COMMAND("MOUSE_Y", []))

    @classmethod
    def getMousePositionX(cls):
        return RUN_COMMAND("MOUSE_X", [])

    @classmethod
    def getMousePositionY(cls):
        return RUN_COMMAND("MOUSE_Y", [])

    @classmethod
    def getMouseDelta(cls):
        return Vector2(RUN_COMMAND("MOUSE_DELTA_X", []), RUN_COMMAND("MOUSE_DELTA_Y", []))

    @classmethod
    def getMouseDeltaX(cls):
        return RUN_COMMAND("MOUSE_DELTA_X", [])

    @classmethod
    def getMouseDeltaY(cls):
        return RUN_COMMAND("MOUSE_DELTA_Y", [])