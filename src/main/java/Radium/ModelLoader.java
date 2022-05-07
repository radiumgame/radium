package Radium;

import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Graphics.MeshRenderer;
import Radium.Graphics.Material;
import Radium.Math.Matrix4;
import Radium.Math.QuaternionUtility;
import Radium.Objects.GameObject;
import Radium.Util.ByteUtility;
import Radium.Util.FileUtility;
import RadiumEditor.Console;
import Radium.Graphics.Mesh;
import Radium.Graphics.Vertex;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.assimp.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Loads models from files such as FBX, OBJ, and DAE
 */
public class ModelLoader {

    protected ModelLoader() {}

    /**
     * Loads a model and adds it to the scene
     * @param filepath Model filepath
     * @return GameObject constructed from model
     */
    public static GameObject LoadModel(String filepath) {
        return LoadModel(filepath, true);
    }

    /**
     * Loads a model and adds it to the scene if instantiate is true
     * @param filePath Model filepath
     * @param instantiate Add it to the scene
     * @return GameObject constructed from model
     */
    public static GameObject LoadModel(String filePath, boolean instantiate) {
        AIScene scene = Assimp.aiImportFile(filePath,
                Assimp.aiProcess_JoinIdenticalVertices |
                        Assimp.aiProcess_Triangulate |
                        Assimp.aiProcess_CalcTangentSpace |
                        Assimp.aiProcess_GenSmoothNormals |
                        Assimp.aiProcess_LimitBoneWeights);

        if (scene == null) {
            Console.Log("Couldn't load model at " + filePath + " | Check if there are muliple meshes in the object. Make sure there is only one mesh in the object.");
            return null;
        }

        GameObject parent = LoadGameObject(scene, scene.mRootNode(), instantiate, new File(filePath));
        parent.name = new File(filePath).getName().split("[.]")[0];

        return parent;
    }

    private static GameObject LoadGameObject(AIScene scene, AINode node, boolean instantiate, File file) {
        GameObject gameObject = new GameObject(instantiate);
        gameObject.name = node.mName().dataString();

        Matrix4f transform = Matrix4.FromAssimp(node.mTransformation());
        Vector3 position = FromJOML(transform.getTranslation(new Vector3f()));
        Quaternionf rotation = transform.getUnnormalizedRotation(new Quaternionf());
        Vector3 scale = FromJOML(transform.getScale(new Vector3f()));

        gameObject.transform.localPosition = position;
        gameObject.transform.localRotation = QuaternionUtility.GetEuler(rotation);
        gameObject.transform.localScale = scale;

        LoadComponents(scene, node, gameObject, file);

        for (int i = 0; i < node.mNumChildren(); i++) {
            GameObject child = LoadGameObject(scene, AINode.create(node.mChildren().get(i)), instantiate, file);
            child.SetParent(gameObject);
        }

        return gameObject;
    }

    private static void LoadComponents(AIScene scene, AINode node, GameObject gameObject, File file) {
        for (int i = 0; i < node.mNumMeshes(); i++) {
            GameObject newMesh = new GameObject();
            newMesh.SetParent(gameObject);

            int meshIndex = node.mMeshes().get(i);
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(meshIndex));
            int vertexCount = mesh.mNumVertices();

            newMesh.name = mesh.mName().dataString();

            AIVector3D.Buffer vertices = mesh.mVertices();
            AIVector3D.Buffer normals = mesh.mNormals();
            AIVector3D.Buffer tangents = mesh.mTangents();
            AIVector3D.Buffer bitangents = mesh.mBitangents();

            Vertex[] vertexList = new Vertex[vertexCount];
            for (int v = 0; v < vertexCount; v++) {
                AIVector3D vertex = vertices.get(v);
                Vector3 meshVertex = new Vector3(vertex.x(), vertex.y(), vertex.z());

                AIVector3D normal = normals.get(v);
                Vector3 meshNormal = new Vector3(normal.x(), normal.y(), normal.z());

                AIVector3D tangent = AIVector3D.create().set(0, 0, 0);
                AIVector3D bitangent = AIVector3D.create().set(0, 0, 0);
                if (tangents != null) {
                    tangent = tangents.get(v);
                }
                if (bitangents != null) {
                    bitangent = bitangents.get(v);
                }

                Vector2 meshTextureCoord = new Vector2(0, 0);
                if (mesh.mNumUVComponents().get(0) != 0) {
                    AIVector3D texture = mesh.mTextureCoords(0).get(v);
                    meshTextureCoord.x = texture.x();
                    meshTextureCoord.y = texture.y();
                }

                vertexList[v] = new Vertex(meshVertex, meshNormal, meshTextureCoord);
                vertexList[v].SetTangent(new Vector3(tangent.x(), tangent.y(), tangent.z()));
                vertexList[v].SetBitangent(new Vector3(bitangent.x(), bitangent.y(), bitangent.z()));
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

            Material finalMaterial = new Material("EngineAssets/Textures/Misc/blank.jpg");
            AIMaterial material = AIMaterial.create(scene.mMaterials().get(mesh.mMaterialIndex()));
            for (int j = 0; j < material.mNumProperties(); j++) {
                AIMaterialProperty property = AIMaterialProperty.create(material.mProperties().get(j));
                String key = property.mKey().dataString();

                if (key.equals(Assimp.AI_MATKEY_COLOR_DIFFUSE)) {
                    FloatBuffer buf = property.mData().asFloatBuffer();
                    Color col = new Color(buf.get(0), buf.get(1), buf.get(2), 1.0f);
                    finalMaterial.color = col;
                } else if (key.equals(Assimp.AI_MATKEY_OPACITY)) {
                    FloatBuffer buf = property.mData().asFloatBuffer();
                    finalMaterial.color.a = buf.get(0);
                } else if (key.equals(Assimp.AI_MATKEY_REFLECTIVITY)) {
                    FloatBuffer buf = property.mData().asFloatBuffer();
                    finalMaterial.reflectivity = buf.get(0);
                }

                AIString path = AIString.create();
                int tex = Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_DIFFUSE, 0, path, (IntBuffer)null, null, null, null, null, null);
                if (!path.equals(null) && tex != -1) {
                    String texPath = file.getParent() + "/" + path.dataString();
                    finalMaterial.path = texPath;
                    finalMaterial.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
                    finalMaterial.CreateMaterial();
                }

                AIString normalPath = AIString.create();
                int nor = Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_NORMALS, 0, normalPath, (IntBuffer)null, null, null, null, null, null);
                if (!normalPath.equals(null) && nor != -1) {
                    String texPath = file.getParent() + "/" + normalPath.dataString();
                    finalMaterial.normalMapPath = texPath;
                    finalMaterial.CreateMaterial();
                }

                AIString specularPath = AIString.create();
                int spec = Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_SPECULAR, 0, specularPath, (IntBuffer)null, null, null, null, null, null);
                if (!specularPath.equals(null) && spec != -1) {
                    String texPath = file.getParent() + "/" + specularPath.dataString();
                    finalMaterial.specularMapPath = texPath;
                    finalMaterial.CreateMaterial();
                }
            }

            Mesh m = new Mesh(vertexList, indicesList);
            MeshFilter filter = new MeshFilter(m);
            filter.material = finalMaterial;
            newMesh.AddComponent(filter);
            newMesh.AddComponent(new MeshRenderer());
        }
    }

    private static Vector3 FromJOML(Vector3f vector) {
        return new Vector3(vector.x(), vector.y(), vector.z());
    }

}
