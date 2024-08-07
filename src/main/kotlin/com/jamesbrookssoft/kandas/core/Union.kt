package com.jamesbrookssoft.kandas.core

/**
 * A type union between 2 or more types.
 *
 * Originally developed by Renato Athaydes - https://github.com/renatoathaydes/kunion
 */
sealed class Union
{
    /**
     * An instance of an union type.
     */
    interface Instance<out V>
    {

        /**
         * The value of a variable whose type is a union type.
         *
         * Notice that the instance itself always has only one type, V.
         */
        val value: V
    }

    /**
     * Rotate the types in this union.
     *
     * The rotation is performed from left to right, so that the last type becomes the first,
     * the first type becomes the second and so on.
     */
    abstract fun rotate(): Union

    /**
     * Convert this Union to a [U2] instance.
     *
     * * [U2] instances return themselves.
     * * [U3] instances of the form U3<A, B, C> return {@code U2<A, U2<B, C>}
     * * [U4] instances of the form U4<A, B, C, D> return {@code U2<A, U3<B, C, D>}
     */
    abstract fun asU2(): Union

    /**
     * Get the instance of this Union, ie. a single-value wrapper.
     */
    abstract fun asInstance(): Instance<*>

    override fun toString(): String = asInstance().value?.toString() ?: "<null>"

    override fun equals(other: Any?) =
            if (other is Union) asInstance().value == other.asInstance().value
            else asInstance().value == other

    override fun hashCode() = asInstance().value?.hashCode() ?: 0

    /**
     * Union of 2 types.
     */
    sealed class U2<out A, out B> : Union()
    {
        companion object
        {
            /**
             * Create a [Union.U2] using the provided value with the first type of the Union (A).
             */
            fun <A> ofA(a: A) = U2.U2_1(a)

            /**
             * Create a [Union.U2] using the provided value with the second type of the Union (B).
             */
            fun <B> ofB(b: B) = U2.U2_2(b)
        }

        override fun rotate(): U2<B, A> = when (this)
        {
            is U2_1<A> -> U2_2(this.value)
            is U2_2<B> -> U2_1(this.value)
        }

        abstract override fun asU2(): U2<A, B>

        /**
         * Use one of the types of this [Union], returning the value of whatever operation ran.
         */
        fun <R> use(useA: (A) -> R, useB: (B) -> R): R
        {
            return when (this)
            {
                is U2_1<A> -> useA(this.value)
                is U2_2<B> -> useB(this.value)
            }
        }

        /**
         * Map this [Union] to another Union using the provided transformations.
         */
        fun <C, D> map(mapA: (A) -> C, mapB: (B) -> D): U2<C, D>
        {
            return when (this)
            {
                is U2_1<A> -> U2.U2_1(mapA(this.value))
                is U2_2<B> -> U2.U2_2(mapB(this.value))
            }
        }

        class U2_1<out A>(override val value: A) : U2<A, Nothing>(), Instance<A>
        {
            override fun asU2(): U2<A, Nothing> = this
            override fun asInstance() = this
        }

        class U2_2<out B>(override val value: B) : U2<Nothing, B>(), Instance<B>
        {
            override fun asU2(): U2<Nothing, B> = this
            override fun asInstance() = this
        }
    }

    /**
     * Union of 3 types.
     */
    sealed class U3<out A, out B, out C> : Union()
    {
        companion object
        {
            /**
             * Create a [Union.U3] using the provided value with the first type of the Union (A).
             */
            fun <A> ofA(a: A) = U3.U3_1(a)

            /**
             * Create a [Union.U3] using the provided value with the second type of the Union (B).
             */
            fun <B> ofB(b: B) = U3.U3_2(b)

            /**
             * Create a [Union.U3] using the provided value with the third type of the Union (C).
             */
            fun <C> ofC(c: C) = U3.U3_3(c)
        }

        override fun rotate(): U3<C, A, B> = when (this)
        {
            is U3_1<A> -> U3_2(this.value)
            is U3_2<B> -> U3_3(this.value)
            is U3_3<C> -> U3_1(this.value)
        }

        abstract override fun asU2(): U2<A, U2<B, C>>

        /**
         * Use one of the types of this [Union], returning the value of whatever operation ran.
         */
        fun <R> use(useA: (A) -> R, useB: (B) -> R, useC: (C) -> R): R
        {
            return when (this)
            {
                is U3.U3_1<A> -> useA(this.value)
                is U3.U3_2<B> -> useB(this.value)
                is U3.U3_3<C> -> useC(this.value)
            }
        }

        /**
         * Map this [Union] to another Union using the provided transformations.
         */
        fun <D, E, F> map(mapA: (A) -> D, mapB: (B) -> E, mapC: (C) -> F): U3<D, E, F>
        {
            return when (this)
            {
                is U3.U3_1<A> -> U3.U3_1(mapA(this.value))
                is U3.U3_2<B> -> U3.U3_2(mapB(this.value))
                is U3.U3_3<C> -> U3.U3_3(mapC(this.value))
            }
        }

        class U3_1<out A>(override val value: A) : U3<A, Nothing, Nothing>(), Instance<A>
        {
            override fun asU2(): U2<A, Nothing> = U2.U2_1(value)
            override fun asInstance() = this
        }

        class U3_2<out B>(override val value: B) : U3<Nothing, B, Nothing>(), Instance<B>
        {
            override fun asU2(): U2<Nothing, U2<B, Nothing>> = U2.U2_2(U2.U2_1(value))
            override fun asInstance() = this
        }

        class U3_3<out C>(override val value: C) : U3<Nothing, Nothing, C>(), Instance<C>
        {
            override fun asU2(): U2<Nothing, U2<Nothing, C>> = U2.U2_2(U2.U2_2(value))
            override fun asInstance() = this
        }
    }

    /**
     * Union of 4 types.
     */
    sealed class U4<out A, out B, out C, out D> : Union()
    {
        companion object
        {
            /**
             * Create a [Union.U4] using the provided value with the first type of the Union (A).
             */
            fun <A> ofA(a: A) = U4.U4_1(a)

            /**
             * Create a [Union.U4] using the provided value with the second type of the Union (B).
             */
            fun <B> ofB(b: B) = U4.U4_2(b)

            /**
             * Create a [Union.U4] using the provided value with the third type of the Union (C).
             */
            fun <C> ofC(c: C) = U4.U4_3(c)

            /**
             * Create a [Union.U4] using the provided value with the fourth type of the Union (D).
             */
            fun <D> ofD(d: D) = U4.U4_4(d)
        }

        override fun rotate(): U4<D, A, B, C> = when (this)
        {
            is U4_1<A> -> U4_2(this.value)
            is U4_2<B> -> U4_3(this.value)
            is U4_3<C> -> U4_4(this.value)
            is U4_4<D> -> U4_1(this.value)
        }

        abstract override fun asU2(): U2<A, U3<B, C, D>>

        /**
         * Use one of the types of this [Union], returning the value of whatever operation ran.
         */
        fun <R> use(useA: (A) -> R, useB: (B) -> R, useC: (C) -> R, useD: (D) -> R): R
        {
            return when (this)
            {
                is U4.U4_1<A> -> useA(this.value)
                is U4.U4_2<B> -> useB(this.value)
                is U4.U4_3<C> -> useC(this.value)
                is U4.U4_4<D> -> useD(this.value)
            }
        }

        /**
         * Map this [Union] to another Union using the provided transformations.
         */
        fun <E, F, G, H> map(mapA: (A) -> E, mapB: (B) -> F, mapC: (C) -> G, mapD: (D) -> H): U4<E, F, G, H>
        {
            return when (this)
            {
                is U4.U4_1<A> -> U4.U4_1(mapA(this.value))
                is U4.U4_2<B> -> U4.U4_2(mapB(this.value))
                is U4.U4_3<C> -> U4.U4_3(mapC(this.value))
                is U4.U4_4<D> -> U4.U4_4(mapD(this.value))
            }
        }

        class U4_1<out A>(override val value: A) : U4<A, Nothing, Nothing, Nothing>(), Instance<A>
        {
            override fun asU2(): U2<A, Nothing> = U2.U2_1(value)
            override fun asInstance() = this
        }

        class U4_2<out B>(override val value: B) : U4<Nothing, B, Nothing, Nothing>(), Instance<B>
        {
            override fun asU2(): U2<Nothing, U3<B, Nothing, Nothing>> = U2.U2_2(U3.U3_1(value))
            override fun asInstance() = this
        }

        class U4_3<out C>(override val value: C) : U4<Nothing, Nothing, C, Nothing>(), Instance<C>
        {
            override fun asU2(): U2<Nothing, U3<Nothing, C, Nothing>> = U2.U2_2(U3.U3_2(value))
            override fun asInstance() = this
        }

        class U4_4<out D>(override val value: D) : U4<Nothing, Nothing, Nothing, D>(), Instance<D>
        {
            override fun asU2(): U2<Nothing, U3<Nothing, Nothing, D>> = U2.U2_2(U3.U3_3(value))
            override fun asInstance() = this
        }
    }

}
