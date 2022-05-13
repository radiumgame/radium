class Component(object):
    def __init__(self):
        self.name = "New Component"
        self.gameObject = None
        self.order = 0
        self.enabled = True

    def enable(self):
        self.enabled = True
        SET_COMPONENT_ATTRIBUTE(self, "enabled", True)

    def disable(self):
        self.enabled = False
        SET_COMPONENT_ATTRIBUTE(self, "enabled", False)

    def setOrder(self, order):
        self.order = order
        SET_COMPONENT_ATTRIBUTE(self, "order", order)

    def call(self, methodName):
        CALL_COMPONENT_METHOD(self, methodName)