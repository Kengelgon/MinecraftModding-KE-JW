/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.model;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;

import com.google.gson.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.math.MathHelper;

import com.google.common.collect.Maps;

import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class TransformationHelper
{
    @Deprecated
    @OnlyIn(Dist.CLIENT)
    public static TransformationMatrix toTransformation(ItemTransformVec3f transform)
    {
        if (transform.equals(ItemTransformVec3f.field_178366_a)) return TransformationMatrix.func_227983_a_();

        return new TransformationMatrix(transform.field_178365_c, quatFromXYZ(transform.field_178364_b, true), transform.field_178363_d, null);
    }

    public static Quaternion quatFromXYZ(Vector3f xyz, boolean degrees)
    {
        return new Quaternion(xyz.func_195899_a(), xyz.func_195900_b(), xyz.func_195902_c(), degrees);
    }

    public static Quaternion quatFromXYZ(float[] xyz, boolean degrees)
    {
        return new Quaternion(xyz[0], xyz[1], xyz[2], degrees);
    }

    public static Quaternion makeQuaternion(float[] values)
    {
        return new Quaternion(values[0], values[1], values[2], values[3]);
    }

    public static Vector3f lerp(Vector3f from, Vector3f to, float progress)
    {
        Vector3f res = from.func_229195_e_();
        res.func_229190_a_(to, progress);
        return res;
    }

    private static final double THRESHOLD = 0.9995;
    public static Quaternion slerp(Quaternion v0, Quaternion v1, float t)
    {
        // From https://en.wikipedia.org/w/index.php?title=Slerp&oldid=928959428
        // License: CC BY-SA 3.0 https://creativecommons.org/licenses/by-sa/3.0/

        // Compute the cosine of the angle between the two vectors.
        // If the dot product is negative, slerp won't take
        // the shorter path. Note that v1 and -v1 are equivalent when
        // the negation is applied to all four components. Fix by
        // reversing one quaternion.
        float dot = v0.func_195889_a() * v1.func_195889_a() + v0.func_195891_b() * v1.func_195891_b() + v0.func_195893_c() * v1.func_195893_c() + v0.func_195894_d() * v1.func_195894_d();
        if (dot < 0.0f) {
            v1 = new Quaternion(-v1.func_195889_a(), -v1.func_195891_b(), -v1.func_195893_c(), -v1.func_195894_d());
            dot = -dot;
        }

        // If the inputs are too close for comfort, linearly interpolate
        // and normalize the result.
        if (dot > THRESHOLD) {
            float x = MathHelper.func_219799_g(t, v0.func_195889_a(), v1.func_195889_a());
            float y = MathHelper.func_219799_g(t, v0.func_195891_b(), v1.func_195891_b());
            float z = MathHelper.func_219799_g(t, v0.func_195893_c(), v1.func_195893_c());
            float w = MathHelper.func_219799_g(t, v0.func_195894_d(), v1.func_195894_d());
            return new Quaternion(x,y,z,w);
        }

        // Since dot is in range [0, DOT_THRESHOLD], acos is safe
        float angle01 = (float)Math.acos(dot);
        float angle0t = angle01*t;
        float sin0t = MathHelper.func_76126_a(angle0t);
        float sin01 = MathHelper.func_76126_a(angle01);
        float sin1t = MathHelper.func_76126_a(angle01 - angle0t);

        float s1 = sin0t / sin01;
        float s0 = sin1t / sin01;

        return new Quaternion(
                s0 * v0.func_195889_a() + s1 * v1.func_195889_a(),
                s0 * v0.func_195891_b() + s1 * v1.func_195891_b(),
                s0 * v0.func_195893_c() + s1 * v1.func_195893_c(),
                s0 * v0.func_195894_d() + s1 * v1.func_195894_d()
        );
    }

    public static TransformationMatrix slerp(TransformationMatrix one, TransformationMatrix that, float progress)
    {
        return new TransformationMatrix(
            lerp(one.getTranslation(), that.getTranslation(), progress),
            slerp(one.func_227989_d_(), that.func_227989_d_(), progress),
            lerp(one.getScale(), that.getScale(), progress),
            slerp(one.getRightRot(), that.getRightRot(), progress)
        );
    }

    public static boolean epsilonEquals(Vector4f v1, Vector4f v2, float epsilon)
    {
        return MathHelper.func_76135_e(v1.func_195910_a()-v2.func_195910_a()) < epsilon &&
               MathHelper.func_76135_e(v1.func_195913_b()-v2.func_195913_b()) < epsilon &&
               MathHelper.func_76135_e(v1.func_195914_c()-v2.func_195914_c()) < epsilon &&
               MathHelper.func_76135_e(v1.func_195915_d()-v2.func_195915_d()) < epsilon;
    }

    public static class Deserializer implements JsonDeserializer<TransformationMatrix>
    {
        @Override
        public TransformationMatrix deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString())
            {
                String transform = json.getAsString();
                if(transform.equals("identity"))
                {
                    return TransformationMatrix.func_227983_a_();
                }
                else
                {
                    throw new JsonParseException("TRSR: unknown default string: " + transform);
                }
            }
            if (json.isJsonArray())
            {
                // direct matrix array
                return new TransformationMatrix(parseMatrix(json));
            }
            if (!json.isJsonObject()) throw new JsonParseException("TRSR: expected array or object, got: " + json);
            JsonObject obj = json.getAsJsonObject();
            TransformationMatrix ret;
            if (obj.has("matrix"))
            {
                // matrix as a sole key
                ret = new TransformationMatrix(parseMatrix(obj.get("matrix")));
                obj.remove("matrix");
                if (obj.entrySet().size() != 0)
                {
                    throw new JsonParseException("TRSR: can't combine matrix and other keys");
                }
                return ret;
            }
            Vector3f translation = null;
            Quaternion leftRot = null;
            Vector3f scale = null;
            Quaternion rightRot = null;
            if (obj.has("translation"))
            {
                translation = new Vector3f(parseFloatArray(obj.get("translation"), 3, "Translation"));
                obj.remove("translation");
            }
            if (obj.has("rotation"))
            {
                leftRot = parseRotation(obj.get("rotation"));
                obj.remove("rotation");
            }
            if (obj.has("scale"))
            {
                if(!obj.get("scale").isJsonArray())
                {
                    try
                    {
                        float s = obj.get("scale").getAsNumber().floatValue();
                        scale = new Vector3f(s, s, s);
                    }
                    catch (ClassCastException ex)
                    {
                        throw new JsonParseException("TRSR scale: expected number or array, got: " + obj.get("scale"));
                    }
                }
                else
                {
                    scale = new Vector3f(parseFloatArray(obj.get("scale"), 3, "Scale"));
                }
                obj.remove("scale");
            }
            if (obj.has("post-rotation"))
            {
                rightRot = parseRotation(obj.get("post-rotation"));
                obj.remove("post-rotation");
            }
            if (!obj.entrySet().isEmpty()) throw new JsonParseException("TRSR: can either have single 'matrix' key, or a combination of 'translation', 'rotation', 'scale', 'post-rotation'");
            return new TransformationMatrix(translation, leftRot, scale, rightRot);
        }

        public static Matrix4f parseMatrix(JsonElement e)
        {
            if (!e.isJsonArray()) throw new JsonParseException("Matrix: expected an array, got: " + e);
            JsonArray m = e.getAsJsonArray();
            if (m.size() != 3) throw new JsonParseException("Matrix: expected an array of length 3, got: " + m.size());
            float[] values = new float[16];
            for (int i = 0; i < 3; i++)
            {
                if (!m.get(i).isJsonArray()) throw new JsonParseException("Matrix row: expected an array, got: " + m.get(i));
                JsonArray r = m.get(i).getAsJsonArray();
                if (r.size() != 4) throw new JsonParseException("Matrix row: expected an array of length 4, got: " + r.size());
                for (int j = 0; j < 4; j++)
                {
                    try
                    {
                        values[j*4+i] = r.get(j).getAsNumber().floatValue();
                    }
                    catch (ClassCastException ex)
                    {
                        throw new JsonParseException("Matrix element: expected number, got: " + r.get(j));
                    }
                }
            }
            return new Matrix4f(values);
        }

        public static float[] parseFloatArray(JsonElement e, int length, String prefix)
        {
            if (!e.isJsonArray()) throw new JsonParseException(prefix + ": expected an array, got: " + e);
            JsonArray t = e.getAsJsonArray();
            if (t.size() != length) throw new JsonParseException(prefix + ": expected an array of length " + length + ", got: " + t.size());
            float[] ret = new float[length];
            for (int i = 0; i < length; i++)
            {
                try
                {
                    ret[i] = t.get(i).getAsNumber().floatValue();
                }
                catch (ClassCastException ex)
                {
                    throw new JsonParseException(prefix + " element: expected number, got: " + t.get(i));
                }
            }
            return ret;
        }

        public static Quaternion parseAxisRotation(JsonElement e)
        {
            if (!e.isJsonObject()) throw new JsonParseException("Axis rotation: object expected, got: " + e);
            JsonObject obj  = e.getAsJsonObject();
            if (obj.entrySet().size() != 1) throw new JsonParseException("Axis rotation: expected single axis object, got: " + e);
            Map.Entry<String, JsonElement> entry = obj.entrySet().iterator().next();
            Quaternion ret;
            try
            {
                if (entry.getKey().equals("x"))
                {
                    ret = Vector3f.field_229179_b_.func_229187_a_(entry.getValue().getAsNumber().floatValue());
                }
                else if (entry.getKey().equals("y"))
                {
                    ret = Vector3f.field_229181_d_.func_229187_a_(entry.getValue().getAsNumber().floatValue());
                }
                else if (entry.getKey().equals("z"))
                {
                    ret = Vector3f.field_229183_f_.func_229187_a_(entry.getValue().getAsNumber().floatValue());
                }
                else throw new JsonParseException("Axis rotation: expected single axis key, got: " + entry.getKey());
            }
            catch(ClassCastException ex)
            {
                throw new JsonParseException("Axis rotation value: expected number, got: " + entry.getValue());
            }
            return ret;
        }

        public static Quaternion parseRotation(JsonElement e)
        {
            if (e.isJsonArray())
            {
                if (e.getAsJsonArray().get(0).isJsonObject())
                {
                    Quaternion ret = Quaternion.field_227060_a_.func_227068_g_();
                    for (JsonElement a : e.getAsJsonArray())
                    {
                        ret.func_195890_a(parseAxisRotation(a));
                    }
                    return ret;
                }
                else if (e.isJsonArray())
                {
                    JsonArray array = e.getAsJsonArray();
                    if (array.size() == 3) //Vanilla rotation
                        return quatFromXYZ(parseFloatArray(e, 3, "Rotation"), true);
                    else // quaternion
                        return makeQuaternion(parseFloatArray(e, 4, "Rotation"));
                }
                else throw new JsonParseException("Rotation: expected array or object, got: " + e);
            }
            else if (e.isJsonObject())
            {
                return parseAxisRotation(e);
            }
            else throw new JsonParseException("Rotation: expected array or object, got: " + e);
        }
    }
}
