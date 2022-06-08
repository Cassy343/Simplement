#ifndef LIBEXPORT_H
#define LIBEXPORT_H

#ifdef SPL_DLL_MEMBER
  #define DLL_EXPORT __declspec(dllexport)
#else
  #define DLL_EXPORT __declspec(dllimport)    
#endif

#endif /* LIBEXPORT_H */