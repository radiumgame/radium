package Radium.Engine;

import Radium.Engine.Color.Color;
import Radium.Engine.Components.Graphics.MeshFilter;
import Radium.Engine.Components.Graphics.MeshRenderer;
import Radium.Engine.Graphics.Material;
import Radium.Engine.Math.Matrix4;
import Radium.Engine.Math.QuaternionUtility;
import Radium.Engine.Objects.GameObject;
import Radium.Editor.Console;
import Radium.Engine.Graphics.Mesh;
import Radium.Engine.Graphics.Vertex;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Math.Vector.Vector3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.assimp.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

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
        return LoadModel(filePath, instantiate, false);
    }

    public static GameObject LoadModel(String filePath, boolean instantiate, boolean loadTextures) {
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

        GameObject parent = LoadGameObject(scene, scene.mRootNode(), instantiate, loadTextures, new File(filePath));
        parent.name = new File(filePath).getName().split("[.]")[0];

        return parent;
    }

    private static GameObject LoadGameObject(AIScene scene, AINode node, boolean instantiate, boolean tex, File file) {
        GameObject gameObject = new GameObject(instantiate);
        gameObject.name = node.mName().dataString();

        Matrix4f transform = Matrix4.FromAssimp(node.mTransformation());
        Vector3 position = FromJOML(transform.getTranslation(new Vector3f()));
        Quaternionf rotation = transform.getUnnormalizedRotation(new Quaternionf());
        Vector3 scale = FromJOML(transform.getScale(new Vector3f()));

        gameObject.transform.localPosition = position;
        gameObject.transform.localRotation = QuaternionUtility.GetEuler(rotation);
        gameObject.transform.localScale = scale;

        LoadComponents(scene, node, gameObject, file, instantiate, tex);

        for (int i = 0; i < node.mNumChildren(); i++) {
            GameObject child = LoadGameObject(scene, AINode.create(node.mChildren().get(i)), instantiate, tex, file);
            child.SetParent(gameObject);
        }

        return gameObject;
    }

    private static void LoadComponents(AIScene scene, AINode node, GameObject gameObject, File file, boolean instantiate, boolean textures) {
        for (int i = 0; i < node.mNumMeshes(); i++) {
            GameObject newMesh = new GameObject(instantiate);
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

            AIMaterial material = AIMaterial.create(scene.mMaterials().get(mesh.mMaterialIndex()));
            Color diffuse = new Color(1, 1, 1, 1.0f);
            try {
                for (int j = 0; j < material.mNumProperties(); j++) {
                    AIMaterialProperty property = AIMaterialProperty.create(material.mProperties().get(j));

                    if (property.mKey().dataString().equals(Assimp.AI_MATKEY_COLOR_DIFFUSE)) {
                        diffuse = GetColor(property.mData());
                    }
                }
            } catch (Exception e) {
                Console.Log("Failed to load all material properties");
            }

            File f = null;
            if (textures) {
                AIString path = AIString.create();
                Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
                f = new File(file.getParent() + "/" + path.dataString());
            }

            // FINAL VARIABLES
            Color finalDiffuse = diffuse;
            File finalF = f;
            OGLCommands.commands.add(() -> {
                Mesh m = new Mesh(vertexList, indicesList);
                Material m1 = new Material("EngineAssets/Textures/Misc/blank.jpg");
                m1.color = finalDiffuse;

                boolean transparent = false;
                if (textures) {
                    if (finalF.exists()) {
                        m1.path = finalF.getPath();

                        try {
                            transparent = ImageIO.read(new FileInputStream(finalF)).getColorModel().hasAlpha();
                        } catch (Exception e) {
                            Console.Error(e);
                        }
                    }
                }
                m1.CreateMaterial();

                MeshFilter mf = new MeshFilter(m);
                mf.material = m1;
                MeshRenderer mr = new MeshRenderer();
                mr.transparent = transparent;

                newMesh.AddComponent(mf);
                newMesh.AddComponent(mr);
            });
        }
    }

    private static Vector3 FromJOML(Vector3f vector) {
        return new Vector3(vector.x(), vector.y(), vector.z());
    }

    private static Color GetColor(ByteBuffer buf) {
        return new Color(buf.getFloat(0), buf.getFloat(4), buf.getFloat(8), buf.getFloat(12));
    }

}
