/* 
 * File:   splnative.h
 * Author: Ian
 *
 * Created on January 25, 2019, 10:38 PM
 */

#ifndef SPLNATIVE_H
#define SPLNATIVE_H

#ifdef __cplusplus
extern "C" {
#endif

#include "libexport.h"
#include "spldefs.h"
#include "mem.h"

DLL_EXPORT void call_native(sid_t id, runtime_t *rt, splthread_t *thread);

#ifdef __cplusplus
}
#endif

#endif /* SPLNATIVE_H */

