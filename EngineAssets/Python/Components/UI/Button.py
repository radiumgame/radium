class Button(Component):
    def __init__(self):
        super(Button, self).__init__()
        self.name = "Button"
        self.callback = lambda e : e.noEvent()

    def setCallback(self, callback):
        self.callback = callback
        RUN_COMMAND("SET_BUTTON_CALLBACK", [self.gameObject.id, self])
