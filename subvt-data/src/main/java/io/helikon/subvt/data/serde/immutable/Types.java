package io.helikon.subvt.data.serde.immutable;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;

@SuppressWarnings({"unchecked"})
class Types {

    private Types() {
    }

    static <E> TypeToken<Collection<E>> collectionOf(Type type) {
        TypeParameter<E> newTypeParameter = new TypeParameter<E>() {
        };
        return new TypeToken<Collection<E>>() {
        }
                .where(newTypeParameter, Types.typeTokenOf(type));
    }

    private static <E> TypeToken<E> typeTokenOf(Type type) {
        return (TypeToken<E>) TypeToken.of(type);
    }
}