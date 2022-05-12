class GameObject:
    def __init__(self):
        self.transform = Transform(Vector3.zero(), Vector3.zero(), Vector3.one())
        self.components = []
        self.name = "New Game Object"
        self.id = None
        self.parent = None
        self.destroyed = False

    def destroy(self):
        self.transform = None
        self.components = None
        self.name = None
        self.destroyed = True
        UPDATE_GAMEOBJECT(self)

    def addComponent(self, component):
        component.gameObject = self
        self.components.append(component)

        SET_COMPONENT_ATTRIBUTE(component, "NEW_COMPONENT", component.name)

        return component

    def getComponent(self, componentName):
        for component in self.components:
            if component.name == componentName:
                return component

        return GET_ENGINE_COMPONENT(componentName)

    def removeComponent(self, component):
        if self.components.__contains__(component):
            self.components.remove(component)

        SET_COMPONENT_ATTRIBUTE(component, "REMOVE_COMPONENT", component.name)

    def setName(self, name):
        self.name = name
        UPDATE_GAMEOBJECT(self)

    def setPosition(self, position):
        self.transform.position = position
        UPDATE_GAMEOBJECT(self)

    def setRotation(self, rotation):
        self.transform.rotation = rotation
        UPDATE_GAMEOBJECT(self)

    def setScale(self, scale):
        self.transform.scale = scale
        UPDATE_GAMEOBJECT(self)

    def translate(self, translation):
        self.transform.position += translation
        UPDATE_GAMEOBJECT(self)

    def rotate(self, rotation):
        self.transform.rotation += rotation
        UPDATE_GAMEOBJECT(self)

    def dilate(self, dilation):
        self.transform.scale *= dilation
        UPDATE_GAMEOBJECT(self)

    def getPosition(self):
        return self.transform.position

    def getRotation(self):
        return self.transform.rotation

    def getScale(self):
        return self.transform.scale

    @classmethod
    def find(name):
        [] = GET_ENGINE_OBJECTS()
        for obj in objects:
            if obj.name == name:
                return obj

        return None
