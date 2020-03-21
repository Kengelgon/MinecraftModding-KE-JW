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

package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;

public class TRSRTransformer extends VertexTransformer
{
    private final TransformationMatrix transform;

    public TRSRTransformer(IVertexConsumer parent, TransformationMatrix transform)
    {
        super(parent);
        this.transform = transform;
    }

    @Override
    public void put(int element, float... data)
    {
        switch (getVertexFormat().func_227894_c_().get(element).func_177375_c())
        {
            case POSITION:
                Vector4f pos = new Vector4f(data[0], data[1], data[2], data[3]);
                transform.transformPosition(pos);
                data[0] = pos.func_195910_a();
                data[1] = pos.func_195913_b();
                data[2] = pos.func_195914_c();
                data[3] = pos.func_195915_d();
                break;
            case NORMAL:
                Vector3f normal = new Vector3f(data);
                transform.transformNormal(normal);
                data[0] = normal.func_195899_a();
                data[1] = normal.func_195900_b();
                data[2] = normal.func_195902_c();
                break;
        }
        super.put(element, data);
    }
}
