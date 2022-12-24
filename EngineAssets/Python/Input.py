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
    def getMousePosition(cls):
        return Vector2(GET_DATA("MOUSE_X"), GET_DATA("MOUSE_Y"))

    @classmethod
    def getMousePositionX(cls):
        return GET_DATA("MOUSE_X")

    @classmethod
    def getMousePositionY(cls):
        return GET_DATA("MOUSE_Y")

    @classmethod
    def getMouseDelta(cls):
        return Vector2(GET_DATA("MOUSE_DELTA_X"), GET_DATA("MOUSE_DELTA_Y"))\

    @classmethod
    def getMouseDeltaX(cls):
        return GET_DATA("MOUSE_DELTA_X")

    @classmethod
    def getMouseDeltaY(cls):
        return GET_DATA("MOUSE_DELTA_Y")