package com.tencent.shadow.core.transform.specific

import com.tencent.shadow.core.transform_kit.AbstractTransformTest
import javassist.ClassPool
import javassist.Loader
import javassist.Modifier
import javassist.NotFoundException
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ReceiverSupportTransformTest : AbstractTransformTest() {

    @Before
    fun setUp() {
        val classPool = ClassPool(null)
        classPool.appendSystemPath()
        val allInputClass = classPool[
                arrayOf(
                    "test.AceReceiver",
                    "test.BarReceiver",
                    "test.CatReceiver",
                    "test.DogReceiver",
                )
        ].toMutableSet()

        val applicationTransform = ReceiverSupportTransform()
        applicationTransform.mClassPool = classPool
        applicationTransform.setup(allInputClass)

        applicationTransform.list.forEach { transform ->
            transform.filter(allInputClass).forEach {
                transform.transform(it)
                it.writeFile(WRITE_FILE_DIR)
            }
        }
    }

    private fun commonTestLogic(testClassName: String, expectLog: Array<String>) {
        dLoader["android.content.BroadcastReceiver"].declaredConstructors[0].setBody(null)
        dLoader["android.content.Context"].declaredConstructors[0].setBody(null)
        dLoader["android.content.ContextWrapper"].declaredConstructors[0].setBody(null)
        dLoader["android.view.ContextThemeWrapper"].declaredConstructors[0].setBody("super(null);")
        dLoader["android.content.Intent"].declaredConstructors[0].setBody(null)

        //加载修改后的类对象
        val ctClass = dLoader[testClassName]
        val loader = Loader(this.javaClass.classLoader, dLoader)
        val clazz = ctClass.toClass(loader)

        //构造实例，调用onReceive方法，检查log记录的字符串List是否符合预期
        val constructor = clazz.getDeclaredConstructor(List::class.java)
        constructor.trySetAccessible()
        val onReceive = clazz.getMethod(
            "onReceive",
            loader.loadClass("android.content.Context"),
            loader.loadClass("android.content.Intent")
        )
        onReceive.trySetAccessible()
        val log = clazz.getDeclaredField("log")
        log.trySetAccessible()
        val receiver = constructor.newInstance(mutableListOf<String>())
        val context =
            loader.loadClass("android.view.ContextThemeWrapper").getDeclaredConstructor().newInstance()
        val intent =
            loader.loadClass("android.content.Intent").getDeclaredConstructor().newInstance()
        onReceive.invoke(receiver, context, intent)
        val logList: List<String> = log.get(receiver) as List<String>
        Assert.assertArrayEquals(expectLog, logList.toTypedArray())
    }

    @Test
    fun testAceReceiver() {
        commonTestLogic(
            "test.AceReceiver",
            arrayOf(
                "AceReceiver onReceive"
            )
        )
    }

    @Test
    fun testBarReceiver() {
        commonTestLogic(
            "test.BarReceiver",
            arrayOf(
                "BarReceiver onReceive"
            )
        )
    }

    @Test
    fun testCatReceiver() {
        commonTestLogic(
            "test.CatReceiver",
            arrayOf(
                "CatReceiver onReceive enter",
                "BarReceiver onReceive",
                "CatReceiver onReceive leave",
            )
        )
    }

    @Test
    fun testDogReceiver() {
        commonTestLogic(
            "test.DogReceiver",
            arrayOf(
                "CatReceiver onReceive enter",
                "BarReceiver onReceive",
                "CatReceiver onReceive leave",
            )
        )
    }

    /**
     * DogReceiver本来就没有override onReceive，我们也不应该给它添加。
     */
    @Test(expected = javassist.NotFoundException::class)
    fun testDogReceiverDoNotHaveOnReceiveMethod() {
        val ctClass = dLoader["test.DogReceiver"]
        ctClass.getDeclaredMethod("onReceive")
    }
}
