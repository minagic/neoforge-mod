package com.minagic.minagic.scaling;

public class DefaultActions {
    public static class OVERRIDE<T> extends StatAction<T, T>{
        public T apply( T object, T data){
            return data;
        }
    }

    public static class MULTIPLY extends StatAction<Float, Float>{
        public Float apply(Float object, Float data){
            return object*data;
        }
    }
    public static class ADD extends StatAction<Float, Float>{
        public Float apply(Float object, Float data){
            return object+data;
        }
    }


}
