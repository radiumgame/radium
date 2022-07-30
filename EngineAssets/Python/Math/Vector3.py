class Vector3:
    def __init__(self, x, y, z):
        self.x = float(x)
        self.y = float(y)
        self.z = float(z)
    
    def __repr__(self):
        return "{ " + str(self.x) + ", " + str(self.y) + ", " + str(self.z) + " }"

    def __add__(self, other):
        return Vector3(self.x + other.x, self.y + other.y, self.z + other.z)

    def __sub__(self, other):
        return Vector3(self.x - other.x, self.y - other.y, self.z - other.z)

    def __mul__(self, other):
        return Vector3(self.x * other.x, self.y * other.y, self.z * other.z)

    def __div__(self, other):
        return Vector3(self.x / other.x, self.y / other.y, self.z / other.z)

    def __eq__(self, other):
        return self.x == other.x and self.y == other.y and self.z == other.z

    def array(self):
        return [self.x, self.y, self.z]

    def length(self):
        return (self.x ** 2 + self.y ** 2 + self.z ** 2) ** 0.5

    def normalize(self):
        length = self.length()
        if length != 0:
            return Vector3(self.x / length, self.y / length, self.z / length)

    def lerp(self, other, t):
        return Vector3(self.x + (other.x - self.x) * t, self.y + (other.y - self.y) * t, self.z + (other.z - self.z) * t)

    def dot(self, other):
        return self.x * other.x + self.y * other.y + self.z * other.z

    def distance(self, other):
        return (self.x - other.x) ** 2 + (self.y - other.y) ** 2 + (self.z - other.z) ** 2

    def cross(self, other):
        return Vector3(self.y * other.z - self.z * other.y, self.z * other.x - self.x * other.z, self.x * other.y - self.y * other.x)

    @classmethod
    def zero(cls):
        return Vector3(0, 0, 0)

    @classmethod
    def one(cls):
        return Vector3(1, 1, 1)