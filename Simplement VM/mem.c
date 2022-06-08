#define SPL_DLL_MEMBER

#include "mem.h"

#if DEBUG
  DLL_EXPORT void* __splmalloc(size_t __size) {
      void *ptr = malloc(__size);
      printf("(M)allocated: %p\n", ptr);
      return ptr;
  }

  DLL_EXPORT void* __splcalloc(size_t __nmemb, size_t __size) {
      void *ptr = calloc(__nmemb, __size);
      printf("(C)allocated: %p\n", ptr);
      return ptr;
  }
  
  DLL_EXPORT void* __splrealloc(void *__r, size_t __size) {
      void *ptr = realloc(__r, __size);
      printf("Reallocated %p to %p\n", __r, ptr);
      return ptr;
  }

  DLL_EXPORT void __splfree(void *ptr) {
      printf("Freeing: %p\n", ptr);
      free(ptr);
  }
#endif

#define __pushfunc(name,type,size) DLL_EXPORT void name(stack_t *stack, type val) \
        {                                                                         \
            memget(stack->top, type) = val;                        \
            stack->top += size;                                                   \
        }

#define __popfunc(name,type,size) DLL_EXPORT type name(stack_t *stack) \
        {                                                              \
            type val = memgetoff(stack->top, -size, type); \
            stack->top -= size;                                        \
            return val;                                                \
        }

__pushfunc(push8,uint8_t,8)
__pushfunc(push16,uint16_t,16)
__pushfunc(push32,uint32_t,32)
__pushfunc(push64,uint64_t,64)
__pushfunc(push32f,float,32)
__pushfunc(push64f,double,64)
__pushfunc(push_intptr,uintptr_t,sizeof(uintptr_t));
__pushfunc(push_ptr,void*,sizeof(uintptr_t))

__popfunc(pop8,int8_t,8)
__popfunc(pop8u,uint8_t,8)
__popfunc(pop16,int16_t,16)
__popfunc(pop16u,uint16_t,16)
__popfunc(pop32,int32_t,32)
__popfunc(pop32u,uint32_t,32)
__popfunc(pop64,int64_t,64)
__popfunc(pop64u,uint64_t,64)
__popfunc(pop32f,float,32)
__popfunc(pop64f,double,64)
__popfunc(pop_intptr,uintptr_t,sizeof(uintptr_t))

DLL_EXPORT void* pop_ptr(stack_t *stack) {
    void *val = (void*)(stack->values + stack->top - sizeof(uintptr_t));
    stack->top += sizeof(uintptr_t);
    return val;
}

DLL_EXPORT splstring_t* new_splstring(const char *str, uint32_t len) {
    splstring_t *splstr = splmalloc(sizeof(splstring_t));
    splstr->len = len;
    splstr->value = splcalloc(len, sizeof(utf8char_t));
    for(uint32_t i = 0;i < len;++ i)
        splstr->value[i] = (utf8char_t)str[i];
    return splstr;
}

DLL_EXPORT void init_stack(stack_t *stack, uint32_t capacity) {
    stack->capacity = capacity * 8;
    stack->values = (uintptr_t)splmalloc(stack->capacity);
    stack->rbtm = stack->values;
    stack->top = stack->values;
    push_intptr(stack, 0);
    push_fa(stack, 0);
    push_ptr(stack, 0);
}

DLL_EXPORT void init_heap(heap_t *heap, uint64_t capacity) {
    heap->capacity = capacity * 8;
    heap->values = (uintptr_t)splmalloc(heap->capacity);
    heap->top = heap->values;
    heap->tracker_capacity = capacity / 64;
    heap->tracker = splcalloc(heap->tracker_capacity, 8);
}

DLL_EXPORT void free_func_spec(func_spec_t *func) {
    splfree(func->param_types);
    splfree(func->name);
}

DLL_EXPORT void free_field_spec(field_spec_t *field) {
    splfree(field->name);
}

DLL_EXPORT void free_class_spec(class_spec_t *clazz) {
    splfree(clazz->fields);
    splfree(clazz->funcs);
}

DLL_EXPORT void free_resource(resource_t *resource) {
    splfree(resource->name);
}

DLL_EXPORT void free_file_data(file_data_t *fdata) {
    splfree(fdata->buffer);
    sid_t i;
    for(i = 0;i < fdata->str_pool_len;++ i)
        free_splstring(&fdata->str_pool[i]);
    splfree(fdata->str_pool);
    for(i = 0;i < fdata->num_resources;++ i)
        free_resource(&fdata->resources[i]);
    splfree(fdata->resources);
    for(i = 0;i < fdata->num_dependencies;++ i)
        free_file_data(&fdata->dependencies[i]);
    splfree(fdata->dependencies);
    for(i = 0;i < fdata->num_funcs;++ i)
        free_func_spec(&fdata->funcs[i]);
    splfree(fdata->funcs);
    for(i = 0;i < fdata->num_classes;++ i)
        free_class_spec(&fdata->classes[i]);
    splfree(fdata->classes);
}

#if DEBUG
DLL_EXPORT void free_func_spec_rt(func_spec_rt_t *func) {
    splfree(func->name);
}
#endif

DLL_EXPORT void free_class_spec_rt(class_spec_rt_t *clazz) {
    splfree(clazz->fields);
    splfree((void*)clazz->static_fields);
#if DEBUG
    splfree(clazz->name);
#endif
}

DLL_EXPORT void free_file_data_rt(file_data_rt_t *fdata) {
    splfree(fdata->buffer);
    splfree((void*)fdata->num_pool);
    sid_t i;
    for(i = 0;i < fdata->str_pool_len;++ i)
        free_splstring(&fdata->str_pool[i]);
    splfree(fdata->str_pool);
    splfree(fdata->resources);
    for(i = 0;i < fdata->num_dependencies;++ i)
        free_file_data_rt(&fdata->dependencies[i]);
    splfree(fdata->dependencies);
#if DEBUG
    for(i = 0;i < fdata->num_funcs;++ i)
        free_func_spec_rt(&fdata->funcs[i]);
#endif
    splfree(fdata->funcs);
    for(i = 0;i < fdata->num_classes;++ i)
        free_class_spec_rt(&fdata->classes[i]);
    splfree(fdata->classes);
}

DLL_EXPORT void free_splobject(splobject_t *obj) {
    splfree((void*)obj->fields);
}

DLL_EXPORT void free_splarray(splarray_t *arr) {
    splfree((void*)arr->values);
}

DLL_EXPORT void free_splstring(splstring_t *str) {
    splfree(str->value);
}

DLL_EXPORT void free_stack(stack_t *stack) {
    splfree((void*)stack->values);
}

DLL_EXPORT void free_heap(heap_t *heap) {
    splfree((void*)heap->values);
    splfree(heap->tracker);
}

DLL_EXPORT void free_splthread(splthread_t *thread) {
    free_stack(&thread->stack);
    free_splstring(thread->name);
    splfree(thread->name);
}

DLL_EXPORT void free_runtime(runtime_t *rt) {
    free_file_data_rt(rt->fdata);
    splfree(rt->fdata);
    while(rt->thread_count > 0)
        free_splthread(&rt->threads[--rt->thread_count]);
    splfree(rt->threads);
    free_heap(&rt->heap);
    splfree(rt);
}