package Engine;

import Engine.Graphics.Material;
import Engine.Graphics.Mesh;
import Engine.Graphics.Vertex;
import Engine.Math.Vector.Vector2;
import Engine.Math.Vector.Vector3;
import org.lwjgl.assimp.*;

public class ModelLoader {

    public static Mesh[] LoadModel(String filePath, String texturePath) {
        AIScene scene = Assimp.aiImportFile(filePath, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate);

        if (scene == null) {
            System.out.println("Couldn't load model at " + filePath + " | Check if there are muliple meshes in the object. Make sure there is only one mesh in the object.");
            return null;
        }

        Mesh[] result = new Mesh[scene.mNumMeshes()];

        for (int i = 0; i < scene.mNumMeshes(); i++) {
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(i));
            int vertexCount = mesh.mNumVertices();

            AIVector3D.Buffer vertices = mesh.mVertices();
            AIVector3D.Buffer normals = mesh.mNormals();

            Vertex[] vertexList = new Vertex[vertexCount];

            for (int v = 0; v < vertexCount; v++) {
                AIVector3D vertex = vertices.get(v);
                Vector3 meshVertex = new Vector3(vertex.x(), vertex.y(), vertex.z());

                AIVector3D normal = normals.get(v);
                Vector3 meshNormal = new Vector3(normal.x(), normal.y(), normal.z());

                Vector2 meshTextureCoord = new Vector2(0, 0);
                if (mesh.mNumUVComponents().get(0) != 0) {
                    AIVector3D texture = mesh.mTextureCoords(0).get(v);
                    meshTextureCoord.x = texture.x();
                    meshTextureCoord.y = texture.y();
                }

                vertexList[v] = new Vertex(meshVertex, meshNormal, meshTextureCoord);
            }

            int faceCount = mesh.mNumFaces();
            AIFace.Buffer indices = mesh.mFaces();
            int[] indicesList = new int[faceCount * 3];

            for (int j = 0; j < faceCount; j++) {
                AIFace face = indices.get(j);
                indicesList[j * 3 + 0] = face.mIndices().get(0);
                indicesList[j * 3 + 1] = face.mIndices().get(1);
                indicesList[j * 3 + 2] = face.mIndices().get(2);
            }

            result[i] = new Mesh(vertexList, indicesList, new Material(texturePath));
        }

        return result;
    }

}
