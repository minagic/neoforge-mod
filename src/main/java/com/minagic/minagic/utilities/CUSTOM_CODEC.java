package com.minagic.minagic.utilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CUSTOM_CODEC {
    public static Codec<Quaternionf> QUATERNION = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("x").forGetter(q -> q.x),
            Codec.FLOAT.fieldOf("y").forGetter(q -> q.y),
            Codec.FLOAT.fieldOf("z").forGetter(q -> q.z),
            Codec.FLOAT.fieldOf("w").forGetter(q -> q.w)
    ).apply(instance, Quaternionf::new));

    public static Codec<Vec3> VEC3 = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("x").forGetter(vec -> vec.x),
            Codec.DOUBLE.fieldOf("y").forGetter(vec -> vec.y),
            Codec.DOUBLE.fieldOf("z").forGetter(vec -> vec.z)
    ).apply(instance, Vec3::new));

    public static Codec<Vector3f> VECTOR3 = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("x").forGetter(vec -> vec.x),
            Codec.FLOAT.fieldOf("y").forGetter(vec -> vec.y),
            Codec.FLOAT.fieldOf("z").forGetter(vec -> vec.z)
    ).apply(instance, Vector3f::new));


}
