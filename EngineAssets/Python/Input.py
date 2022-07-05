class Input:
    def __init__(self):
        raise Exception("Input class is not meant to be instantiated")

    @classmethod
    def isKeyDown(cls, key):
        return GET_KEYBOARD_INPUT("down", key)

    @classmethod
    def isMouseDown(cls, button):
        return GET_MOUSE_INPUT("down", button)