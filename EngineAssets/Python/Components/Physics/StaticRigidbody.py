class StaticRigidbody(Component):
    def __init__(self):
        super(StaticRigidbody, self).__init__()
        self.name = "Static Rigidbody"
        self.colliderRadius = 0.5
        self.colliderSize = Vector3(1, 1, 1)

    def setColliderRadius(self, colliderRadius):
        self.colliderRadius = colliderRadius
        SET_COMPONENT_ATTRIBUTE(self, "radius", colliderRadius)

    def setColliderSize(self, colliderSize):
        self.colliderSize = colliderSize
        SET_COMPONENT_ATTRIBUTE(self, "colliderScale", colliderSize)