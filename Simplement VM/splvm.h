/* 
 * File:   splvm.h
 * Author: Ian
 *
 * Created on January 25, 2019, 3:28 PM
 */

#ifndef SPLVM_H
#define SPLVM_H

#ifdef __cplusplus
extern "C" {
#endif

#include "spldefs.h"
#include "libexport.h"
#include "mem.h"
#include "splio.h"

DLL_EXPORT void execute_thread(runtime_t *, splthread_t *);

#ifdef __cplusplus
}
#endif

#endif /* SPLVM_H */

