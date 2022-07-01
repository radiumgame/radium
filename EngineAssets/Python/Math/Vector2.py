class Vector2:
    def __init__(self, x, y):
        self.x = x
        self.y = y

    def __repr__(self):
        return "{ " + str(self.x) + ", " + str(self.y) + " }"

    def __add__(self, other):
        return Vector2(self.x + other.x, self.y + other.y)

    def __sub__(self, other):
        return Vector2(self.x - other.x, self.y - other.y)

    def __mul__(self, other):
        return Vector2(self.x * other.x, self.y * other.y)

    def __div__(self, other):
        return Vector2(self.x / other.x, self.y / other.y)

    def __eq__(self, other):
        return self.x == other.x and self.y == other.y

    def array(self):
        return [self.x, self.y]

    def length(self):
        return (self.x ** 2 + self.y ** 2) ** 0.5

    def normalize(self):
        length = self.length()
        if length != 0:
            return Vector2(self.x / length, self.y / length)

    def lerp(self, other, t):
        return Vector2(self.x + (other.x - self.x) * t, self.y + (other.y - self.y) * t)

    def dot(self, other):
        return self.x * other.x + self.y * other.y

    def distance(self, other):
        return (self.x - other.x) ** 2 + (self.y - other.y) ** 2

    def cross(self, other):
        return Vector2(self.x * other.y - self.y * other.x, self.y * other.x - self.x * other.y)

    @classmethod
    def zero(cls):
        return Vector2(0, 0)

    @classmethod
    def one(cls):
        return Vector2(1, 1)