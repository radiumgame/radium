class Camera(Component):
    def __init__(self):
        super(Camera, self).__init__()
        self.name = "Camera"
        self.nearPlane = 0.1
        self.farPlane = 100.0
        self.fov = 70.0

    def setNearPlane(self, nearPlane):
        self.nearPlane = nearPlane
        SET_COMPONENT_ATTRIBUTE(self, "near", self.nearPlane)

    def setFarPlane(self, farPlane):
        self.farPlane = farPlane
        SET_COMPONENT_ATTRIBUTE(self, "far", self.farPlane)

    def setFov(self, fov):
        self.fov = fov
        SET_COMPONENT_ATTRIBUTE(self, "fov", self.fov)